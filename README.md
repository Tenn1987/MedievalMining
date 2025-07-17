# MedievalMining

**MedievalMining** is a custom Minecraft plugin that simulates historical city economies, NPC labor systems,
and trade networks inspired by 1500s world powers.

---

## 🌍 Vision

This plugin is part of a long-term portfolio project by Brandon Hilliard, a WGU Network & Security student.
It serves as a sandbox for exploring game logic, Java development, server-side performance tuning,
and economic modeling in multiplayer environments.

---

## 🔧 Features

### 🏰 City & Nation System
- Auto-generates historical cities (e.g., London, Paris, Istanbul)
- Supports custom nations with unique currencies

### 👷 NPC Worker System
- Role-based AI workers: Farmer, Miner, Guard, Merchant, Builder, Caravaner
- Economchat gpt
- ic production based on resources and supply/demand
- NPCs interact with city inventories and treasuries

### 🪙 Dynamic Economy
- Commodity pricing adapts to inventory levels
- Support for physical and virtual currencies
- Currency adoption/enforcement for client cities

### 📦 Market System
- Chest-based NPC shops and trade chests
- Dynamic signage for shop pricing
- Merchant and Caravaner NPCs travel/trade autonomously

### 🧱 City Management
- Chunk claiming and protection
- Custom banners and naming
- Treasury tracking and taxation logic

---

## 🚀 Getting Started

1. Install on a Minecraft server (Spigot or Paper 1.20+)
2. Place the plugin `.jar` in the `/plugins` folder
3. Install dependencies:
  - [Vault](https://www.spigotmc.org/resources/vault.34315/)
  - [Citizens2](https://github.com/CitizensDev/Citizens2)
  - [Dynmap (optional)](https://github.com/webbukkit/dynmap)
4. Start the server and use `/city create` to begin

---

## 🧪 In Progress
- AI faction behavior for diplomacy and warfare
- Job re-assignment and unemployment tracking
- GUI interfaces for city and treasury stats
- Player empires with capital-controlled economies

---

## 📂 File Structure

```
src/main/java/com/brandon/...    → Core plugin logic
libs/                           → Plugin dependencies
resourcepack/                   → Optional coin textures
```

---

## 🛠️ Requirements

- Minecraft 1.20+
- Java 17+
- Citizens2, Vault
- Dynmap (optional)

---

## 🧑 Author

**Brandon Hilliard**  
Veteran, father, and aspiring network engineer  
Email: brandon.hilliard@yahoo.com  
LinkedIn: [Brandon Hilliard](https://www.linkedin.com/in/brandon-hilliard-loto-missouri)  
GitHub: [Tenn1987](https://github.com/Tenn1987)

---

## 📜 License

MIT License. Use freely. Attribution appreciated for forks or public deployments.

