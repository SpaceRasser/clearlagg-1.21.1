# ClearLagMod

**ClearLagMod** is a lightweight NeoForge mod for Minecraft 1.21.1 designed to keep your server lag-free by automatically removing dropped items. Customize cleanup intervals, warning times, and enjoy multilingual support.

---

## 🌟 Features

* **Automated Item Cleanup**

  * Periodically clears all `ItemEntity` instances in loaded dimensions to reduce entity lag.
* **Configurable Timing**

  * Set cleanup frequency and warning countdowns via a simple TOML config.
* **Player Alerts**

  * Broadcasts customizable warnings (e.g., 5 minutes, 1 minute, 10 seconds) before each cleanup.
* **Dynamic Config Reload**

  * Apply changes to `warningSeconds` and `clearIntervalSeconds` on-the-fly without restarting the server.
* **Multi-language Support**

  * English & Russian included by default; easily add more via standard JSON language files.

---

## ⚙️ Configuration

Upon first run, a config file is generated at:

```
config/clearlagmod-common.toml
```

Customize the following settings:

```toml
[general]
# Interval between cleanups (seconds): 60–86400
clearIntervalSeconds = 1800

# Warning times before cleanup (seconds)
warningSeconds = [300, 60, 10]
```

* **clearIntervalSeconds**: How often to perform the item purge.
* **warningSeconds**: When to broadcast warnings before each cleanup.

*Save* the file and changes will activate automatically on the next configuration reload.

---

## 📥 Installation

1. **Download** `clearlagmod-<version>.jar` from the Releases page.
2. **Place** into your server’s `mods/` directory (requires NeoForge MDK 21.1.176+).
3. **Start** the server; config and lang files will generate automatically.

No additional commands are needed.

---

## 🚀 Usage

* **Default**: Cleans items every 30 minutes with warnings at 5m, 1m, 10s.
* **Customize**: Edit `config/clearlagmod-common.toml` and reload configs (`/reload config`).

Players simply need to wait for alerts; no permissions or commands required.

---

## 🤝 Contributing

1. **Fork** the repo & create a feature branch.
2. **Implement** your changes.
3. **Submit** a pull request to `main`.

Please adhere to existing code conventions and include descriptive commit messages.

---

## 📄 License

Distributed under the [MIT License](LICENSE).
