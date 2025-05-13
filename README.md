# MagicWands - Minecraft Zauberstab-Plugin

## 📖 Beschreibung
MagicWands ist ein Minecraft-Plugin, das ein umfassendes Zauberstab-System mit modularem Crafting und Upgrade-Möglichkeiten bietet. Spieler können ihre eigenen Zauberstäbe erstellen, mit verschiedenen Zaubersprüchen ausstatten und diese im Kampf oder für andere Zwecke einsetzen.

## 🧙‍♂️ Tutorial: Wie benutze ich das Plugin?

### Zauberstäbe erstellen
1. Verwende den Befehl `/wandbuilder`, um das Crafting-GUI zu öffnen
2. Platziere die drei Komponenten in die entsprechenden Slots:
   - **Kern (Core)**: Bestimmt die Mana-Kapazität des Zauberstabs
   - **Griff (Grip)**: Beeinflusst die Cast-Geschwindigkeit und Krit-Chance
   - **Fokus (Focus)**: Legt den Elementarschaden fest
3. Klicke auf den "Erstellen"-Button, um deinen Zauberstab zu craften

### Zaubersprüche wechseln
1. Halte deinen Zauberstab in der Hand
2. Drücke die Schleichen-Taste (standardmäßig Shift), um zwischen den verfügbaren Zaubersprüchen zu wechseln
3. Der aktuell ausgewählte Zauberspruch wird im Titel-Bereich angezeigt

### Zaubersprüche wirken
1. Wähle einen Zauberspruch aus (siehe oben)
2. Rechtsklicke mit dem Zauberstab, um den Zauber zu wirken
3. Beachte die Cooldown-Zeit zwischen den Zaubern

### Zauberstäbe verbessern
1. Sammle Upgrade-Runen (verschiedene Typen wie Mana-Rune, Feuer-Rune, etc.)
2. Halte deinen Zauberstab in der Haupthand
3. Rechtsklicke mit der Rune, um deinen Zauberstab zu verbessern
4. Die verbesserten Werte werden in der Lore des Zauberstabs angezeigt

## 🔧 Verfügbare Zaubersprüche
- **Feuerball**: Wirft einen Feuerball, der Schaden verursacht und Gegner in Brand setzt
- **Eissplitter**: Verlangsamt Gegner und verursacht Wasserschaden
- **Steinschlag**: Verursacht hohen Erdschaden
- **Windschnitt**: Schneller Zauber mit geringem Cooldown
- **Arkane Explosion**: Starker Flächenzauber mit arkaner Energie
- **Blitzschlag**: Hoher Schaden mit Chance auf Kettenblitze
- **Leerenkugel**: Mächtiger Dunkler Zauber mit hohen Kosten

## 🛠️ Admin-Befehle
- `/mw wandinfo` - Zeigt alle Statistiken eines Zauberstabs an
- `/mw givewand <core> <grip> <focus>` - Erstellt einen Zauberstab mit den angegebenen Komponenten

## 📋 ToDo-Liste

### 🔧 Grundsystem
- [x] Erstelle `Wand`-Klasse für Stats wie `mana`, `elementdamage`, `castSpeed` etc.
- [x] Implementiere `Wand`-Klasse, die Stats + Spellslots + Komponenten kapselt
- [x] Speichere Wand-Daten via `NBT` im Item

### Modulares Crafting-System
#### Komponenten-Enums
- [x] `WandCore` mit `manaCapacity`
- [x] `WandGrip` mit `castSpeed`, evtl. CritChance
- [x] `WandFocus` mit `elementDamage`, Typ (Feuer, Eis, ...)

#### Crafting GUI
- [x] GUI-Command `/wandbuilder`
- [x] Inventar mit 3 Slots (Core, Grip, Focus) + Button „Erstellen“
- [ ] Verarbeitung der Komponenten beim Klick
- [ ] Erstellung des fertigen Wand-Items inkl. Lore & NBT

### Spell-System (Grundgerüst)
- [x] Spell-Registry mit Spell-Klassen
- [x] Jeder Wand kann Spell-IDs in `spellSlots` enthalten
- [x] Casting-System, das aktuellen Spell ausführt (Mana prüfen, Cooldown, Stats einbeziehen)
- [x] Spell switcher über Sneaken mit Wand in der hand und liste wird mit view direction in titel angezeigt

### Upgrade-System
- [ ] Item-Typ: `Upgrade Rune` (verschiedene Varianten: Mana-Rune, Feuer-Rune, etc.)
- [ ] Rechtsklick mit Rune: prüfe, ob Wand in Mainhand
- [ ] Lese aktuelle Stats aus Wand, verbessere gezielt einen Stat
- [ ] Erstelle neue Wand mit verbesserten Stats und ersetze alte

### Debug / Dev Tools
- [x] Command `/mw wandinfo` zeigt alle Stats eines Zauberstabs an
- [x] Command `/mw givewand core grip focus` zum schnellen Testen
- [ ] Logging oder Feedback bei Crafting / Upgrades



