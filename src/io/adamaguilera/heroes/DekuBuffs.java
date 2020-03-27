package io.adamaguilera.heroes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class DekuBuffs {
    Player p;
    int permCost;
    int currCost;

    public DekuBuffs (Player p) {
        this.p = p;
        permCost = 0;
    }

    public void activate (ItemStack itemStack) {
        // type is guaranteed not to be null because function call requires
        // isBuff to be true;
        PotionEffectType type = getBuff (itemStack.getType());
        int strength = itemStack.getAmount();

    }



    private List<PotionEffect> getDebuff (int cost) {
        return null;
    }
    private int getCost (PotionEffectType type, int strength) {

        if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
            switch (strength) {
                case 0:
                    return 5;
                case 1:
                    return 15;
                case 2:
                    return 30;
                default:
                    return 100;
            }
        } else if (type.equals(PotionEffectType.REGENERATION)) {
            switch (strength) {
                case 0:
                    return 5;
                case 1:
                    return 15;
                case 2:
                    return 23;
                case 3:
                    return 30;
                default:
                    return 100;
            }
        } else if (type.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
            switch (strength) {
                case 0:
                    return 5;
                case 1:
                    return 22;
                default:
                    return 100;
            }
        } else if (type.equals(PotionEffectType.SPEED)) {
            switch (strength) {
                case 0:
                    return 1;
                case 1:
                    return 5;
                case 2:
                    return 10;
                case 3:
                    return 15;
                default:
                    return 100;
            }
        } else if (type.equals(PotionEffectType.ABSORPTION)) {
            switch (strength) {
                case 0:
                    return 1;
                case 1:
                    return 5;
                case 2:
                    return 8;
                default:
                    return 100;
            }
        } else if (type.equals(PotionEffectType.FAST_DIGGING)) {
            switch (strength) {
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 3;
                case 3:
                case 4:
                    return 4;
                case 5:
                case 6:
                    return 5;
                default:
                    return 100;
            }
        } else if (type.equals(PotionEffectType.JUMP)) {
            switch (strength) {
                case 0:
                case 1:
                    return 1;
                case 2:
                case 3:
                    return 2;
                case 4:
                case 5:
                    return 3;
                case 6:
                case 7:
                    return 5;
                case 8:
                case 9:
                    return 8;
                default:
                    return 100;
            }
        } else {
            // none of the effects, return 0;
            return 0;
        }
    }

    public boolean isBuff (Material type) {
        if (getBuff (type) == null) {
            return false;
        }
        return true;
    }

    public PotionEffectType getBuff (Material type) {
        switch (type) {
            case RED_DYE:
                return PotionEffectType.INCREASE_DAMAGE;
            case PINK_DYE:
                return PotionEffectType.REGENERATION;
            case GRAY_DYE:
                return PotionEffectType.DAMAGE_RESISTANCE;
            case LIGHT_BLUE_DYE:
                return PotionEffectType.SPEED;
            case YELLOW_DYE:
                return PotionEffectType.ABSORPTION;
            case ORANGE_DYE:
                return PotionEffectType.FAST_DIGGING;
            case LIME_DYE:
                return PotionEffectType.JUMP;
            default:
                return null;

        }
    }
}
