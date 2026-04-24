# Плагины GryazWorld

## Архитектура серверов

| Сервер    | Контейнер  | Порт  | Тип                      |
|-----------|-----------|-------|--------------------------|
| gamegraz  | 73ec1a4d  | 25566 | Paper 1.21.1 — основной  |
| farmserv  | 53571f93  | 25567 | Paper 1.21.1 — фарм      |
| velocity  | 718abfd1  | 25565 | Velocity прокси           |

---

## Готовые плагины (скачать и положить)

| # | Плагин | Ссылка | Сервер |
|---|--------|--------|--------|
| 1 | InvisibleItemFramesLite | https://modrinth.com/plugin/invisibleitemframes | gamegraz |
| 2 | NametagHider | https://modrinth.com/plugin/nametaghider | gamegraz |
| 3 | SpitSTIK | https://modrinth.com/plugin/spitstik | gamegraz |
| 4 | FancyHolograms | https://hangar.papermc.io/Oliver/FancyHolograms | gamegraz |
| 5 | WanderingTrades | https://modrinth.com/plugin/wanderingtrades | gamegraz |
| 6 | TAB (NEZNAMY) | https://hangar.papermc.io/NEZNAMY/TAB | Velocity + все бэкенды |
| 7 | CoreProtect | https://hangar.papermc.io/CORE/CoreProtect | gamegraz + farmserv |
| 8 | RP Plugin | https://modrinth.com/plugin/rp-plugin | gamegraz |
| 9 | GSit | https://www.spigotmc.org/resources/gsit.62325/ | gamegraz |
| 10 | ArmorStandEditor-Reborn | https://modrinth.com/plugin/armorstandeditor-reborn | gamegraz |
| 11 | SkinsRestorer | https://modrinth.com/plugin/skinsrestorer | Velocity + все бэкенды |
| 12 | SimpleProxyChat | https://modrinth.com/plugin/simpleproxychat | Velocity + helper на бэкенды |
| 13 | NetworkChat | https://modrinth.com/plugin/networkchat | Velocity + все бэкенды |
| 14 | BreweryX | https://hangar.papermc.io/BreweryTeam/BreweryX | gamegraz |
| 15 | SimpleHarvest | https://modrinth.com/plugin/simpleharvest | gamegraz + farmserv |
| 16 | Plasmo Voice | https://modrinth.com/plugin/plasmo-voice | gamegraz + farmserv |
| 17 | **LuckPerms** | https://luckperms.net | Velocity + все бэкенды (первым!) |
| 18 | **PlaceholderAPI** | https://hangar.papermc.io/HelpMe/PlaceholderAPI | gamegraz + farmserv (первым!) |

---

## CUSTOM — писать в GryazPlugin

- Крафт невидимых блоков света
- РП-персонажи (до 3 на игрока, Chest GUI, SQLite)
- Наручники через поводок `/handcuff` `/unhandcuff` — только police и admin, пермишн `gryaz.handcuff`
- Кальян (кастомный предмет, эффекты, частицы)
- Надевать предметы на голову (Shift+ПКМ)
- Toggle фантомов `/togglephantom` (флаг в SQLite)
- MiniMessage в чате только для admin/moder (пермишн `chat.minimessage`)

---

## Без плагина

- **Видимость на карте** — Pl3xMap уже стоит, `/map hide` / `/map show`, пермишн `pl3xmap.hide` всем игрокам
- **Роли** — LuckPerms группы: police, banker, moder, keeper
- **Цвет ника / префиксы** — TAB + LuckPerms meta, только admin
