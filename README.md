# 电视直播播放器 (TV Live Player)

一个功能完整的Android电视直播播放器，支持遥控器操作、频道管理和Web后台管理系统。

## 功能特性

### Android应用功能
- 📺 电视直播播放
- 🎮 遥控器操作（方向键换台、确认键显示频道列表
- 📋 频道列表管理
- 🔄 支持多种流媒体协议（HTTP、UDP、RTP组播
- 💾 本地数据库存储频道
- 🌐 Web后台同步功能

### Web后台管理功能
- 🖥️ 美观的Web管理界面
- ➕ 添加、编辑、删除频道
- 🔗 配置频道名称、流地址、流类型
- 📱 与Android应用数据同步

## 项目结构

```
.
├── app/                          # Android应用
│   ├── src/
│   │   └── main/
│   │       ├── java/com/tvlive/player/
│   │       │   ├── MainActivity.kt          # 主界面
│   │       │   ├── ChannelManageActivity.kt # 频道管理
│   │       │   ├── SettingsActivity.kt     # 设置界面
│   │       │   ├── adapter/               # 适配器
│   │       │   ├── api/                   # API接口
│   │       │   ├── database/              # 数据库
│   │       │   └── model/                # 数据模型
│   │       ├── res/                     # 资源文件
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── web-backend/                 # Web后台
│   ├── public/                 # 前端文件
│   ├── server.js              # 服务器端
│   └── package.json
├── build.gradle
├── settings.gradle
└── README.md
```

## 快速开始

### 1. Web后台服务启动

```bash
cd web-backend
npm install
npm start
```

服务将在 http://localhost:8080 启动，打开浏览器访问该地址即可使用Web管理界面。

### 2. Android应用编译

```bash
# 在项目根目录
./gradlew assembleDebug
```

或使用Android Studio打开项目进行编译。

## 使用说明

### Android应用使用

1. **安装APK到Android TV或Android电视盒子
2. 启动应用，默认会播放预设的示例频道
3. 使用遥控器操作：
   - **方向键上/下**：切换频道
   - **频道键上/下**：切换频道
   - **确认键/OK键**：显示频道列表
   - **菜单键**：显示/隐藏频道列表
   - **返回键**：隐藏频道列表

3. **频道管理**：在频道列表中点击"频道管理"按钮进入管理界面
4. **设置**：配置Web后台地址，同步频道列表

### Web后台使用

1. 访问 http://localhost:8080
2. 点击"添加频道"按钮添加新频道
3. 填写频道名称、流地址、选择流类型
4. 保存后，在Android应用中设置相同的服务器地址并同步

## 支持的流媒体格式

- **HTTP/HTTPS**：HLS、DASH等常见流媒体
- **UDP**：UDP组播（如 `udp://@239.0.0.1:1234`）
- **RTP**：RTP流

## 技术栈

### Android端
- **语言：Kotlin
- **播放器**：ExoPlayer
- **网络请求**：Retrofit
- **数据库**：SQLite
- **UI框架**：AndroidX Leanback

### Web后台
- **后端**：Node.js + Express
- **前端**：原生 HTML/CSS/JavaScript
- **数据存储**：JSON文件

## 开发环境要求

- Android Studio Hedgehog (2023.1.1) 或更高
- Android SDK API 24+
- Node.js 16+
- Gradle 8.0+

## 注意事项

1. **网络权限**：确保应用有足够的网络权限，特别是在使用组播时
2. **防火墙设置**：确保组播流量能正常通过防火墙
3. **Web后台**：建议将Web后台部署在局域网内，确保Android设备能访问

## 许可证

MIT License
