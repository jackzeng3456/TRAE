const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 8080;

app.use(cors());
app.use(bodyParser.json());
app.use(express.static('public'));

const DATA_FILE = path.join(__dirname, 'data', 'channels.json');

const ensureDataDirectory = () => {
    const dataDir = path.join(__dirname, 'data');
    if (!fs.existsSync(dataDir)) {
        fs.mkdirSync(dataDir);
    }
    if (!fs.existsSync(DATA_FILE)) {
        const defaultChannels = [
            {
                id: Date.now(),
                name: '中央一台',
                streamUrl: 'http://example.com/cctv1',
                streamType: 'HTTP',
                logoUrl: null,
                isFavorite: false
            },
            {
                id: Date.now() + 1,
                name: '中央二台',
                streamUrl: 'http://example.com/cctv2',
                streamType: 'HTTP',
                logoUrl: null,
                isFavorite: false
            }
        ];
        fs.writeFileSync(DATA_FILE, JSON.stringify(defaultChannels, null, 2));
    }
};

const readChannels = () => {
    try {
        const data = fs.readFileSync(DATA_FILE, 'utf8');
        return JSON.parse(data);
    } catch (error) {
        return [];
    }
};

const writeChannels = (channels) => {
    fs.writeFileSync(DATA_FILE, JSON.stringify(channels, null, 2));
};

app.get('/api/channels', (req, res) => {
    const channels = readChannels();
    res.json(channels);
});

app.post('/api/channels', (req, res) => {
    const channels = readChannels();
    const newChannel = {
        id: Date.now(),
        ...req.body
    };
    channels.push(newChannel);
    writeChannels(channels);
    res.json({ success: true, message: 'Channel added', channel: newChannel });
});

app.put('/api/channels/:id', (req, res) => {
    const channels = readChannels();
    const index = channels.findIndex(c => c.id == req.params.id);
    if (index !== -1) {
        channels[index] = { ...channels[index], ...req.body };
        writeChannels(channels);
        res.json({ success: true, message: 'Channel updated' });
    } else {
        res.status(404).json({ success: false, message: 'Channel not found' });
    }
});

app.delete('/api/channels/:id', (req, res) => {
    let channels = readChannels();
    const index = channels.findIndex(c => c.id == req.params.id);
    if (index !== -1) {
        channels.splice(index, 1);
        writeChannels(channels);
        res.json({ success: true, message: 'Channel deleted' });
    } else {
        res.status(404).json({ success: false, message: 'Channel not found' });
    }
});

app.post('/api/channels/batch', (req, res) => {
    const channels = req.body;
    writeChannels(channels);
    res.json({ success: true, message: 'Channels updated' });
});

ensureDataDirectory();

app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
    console.log(`Open http://localhost:${PORT} to access the management interface`);
});
