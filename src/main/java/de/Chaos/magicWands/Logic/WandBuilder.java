package de.Chaos.magicWands.Logic;

import de.Chaos.magicWands.Enums.WandCore;
import de.Chaos.magicWands.Enums.WandFocus;
import de.Chaos.magicWands.Enums.WandGrip;

public class WandBuilder {
    public static Wand buildWand(WandCore core, WandGrip grip, WandFocus focus) {
        return new Wand(core, grip, focus);
    }
}
