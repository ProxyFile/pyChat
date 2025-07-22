<div align="center">
  <h1>pyChat</h1>
  <p>A simple, lightweight, and highly customizable chat management plugin for Spigot/Paper servers.</p>
  <p>
    <a href="#"><img src="https://img.shields.io/badge/Spigot-1.18%2B-orange" alt="Spigot Version"></a>
    <a href="#"><img src="https://img.shields.io/badge/Java-17-blue" alt="Java Version"></a>
    <a href="https://github.com/ProxyFile/pyChat/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-MIT-green" alt="License"></a>
  </p>
</div>

---

## ✨ Features

- **🎨 Customizable Chat Format**: Take full control over how player messages appear. Integrates with LuckPerms/Vault for prefixes and suffixes.
- **👋 Custom Join/Leave Messages**: Set unique messages for players joining and leaving the server.
- **🧹 Automatic Chat Clear**: Keep your chat clean by automatically clearing it at a set interval.
- **🚫 Spam Protection**: Prevent players from spamming messages too quickly with a configurable delay and automatic kick system.
- **🤬 Word Blacklist**: Block messages containing forbidden words or phrases to maintain a friendly environment.
- **🔗 Link & IP Protection**: Stop players from advertising other servers or websites, with a configurable whitelist for your own links.
- **🙈 Chat Toggle**: Allow players to hide and show the chat for themselves with a simple command.
- **💬 Discord Integration**: Relay in-game chat, kicks, and other events to a Discord channel via a webhook.
- **⚙️ Fully Configurable**: Almost every feature can be enabled, disabled, or tweaked in the `config.yml`.
- **🚀 Lightweight**: Designed to be efficient and not impact your server's performance.

---

## 💾 Installation

1. Download the latest `pyChat.jar` from the [Releases](https://github.com/your-username/pyChat/releases) page.
2. Place the `.jar` file into your server's `/plugins` directory.
3. Restart or reload your server.
4. A `config.yml` file will be generated in `/plugins/pyChat/`. Configure it to your liking!
5. Run `/chat reload` to apply your changes.

---

## 🔧 Commands & Permissions

| Command          | Description                             | Permission             |
| ---------------- | --------------------------------------- | ---------------------- |
| `/chat toggle`   | Toggles your own chat visibility.       | `pychat.toggle`        |
| `/chat clear`    | Manually clears the global chat.        | `pychat.admin.clear`   |
| `/chat reload`   | Reloads the `config.yml` from disk.     | `pychat.admin.reload`  |
| **(Bypass)**     | Bypasses all protection features.       | `pychat.bypass`        |
| **(Admin)**      | Grants access to all admin commands.    | `pychat.admin.*`       |

---

## 📝 Placeholders

pyChat supports several placeholders to customize messages.

| Placeholder | Description                                                                |
| ----------- | -------------------------------------------------------------------------- |
| `{username}`| The player's username.                                                     |
| `{message}` | The content of the player's chat message.                                  |
| `{prefix}`  | The player's prefix from your permissions plugin (e.g., LuckPerms).        |
| `{suffix}`  | The player's suffix from your permissions plugin (e.g., LuckPerms).        |

---
