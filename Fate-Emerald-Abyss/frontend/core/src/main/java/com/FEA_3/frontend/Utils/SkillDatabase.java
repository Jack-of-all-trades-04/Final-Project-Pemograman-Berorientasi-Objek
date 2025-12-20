package com.FEA_3.frontend.Utils;

import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.Skill;

public class SkillDatabase {

    /**
     * Method ini khusus memuat skill set untuk class SABER (Artoria)
     */
    public static void loadSaberSkills(GameUnit hero) {

        // --- 1. DIVINE LIGHT (Support) ---
        hero.addSkill(new Skill("Divine Light", "Heal 15% HP & lower enemy accuracy.", "Icons/DivineLight.png",
            2, 20, Skill.SkillType.ACTIVE, (user, target) -> {
            int healAmount = (int) (user.getStats().getMaxHp() * 0.15);
            int newHp = user.getStats().getCurrentHp() + healAmount;
            if(newHp > user.getStats().getMaxHp()) newHp = user.getStats().getMaxHp();
            user.getStats().setCurrentHp(newHp);

            target.setAccuracyDebuff(3); // Durasi 3 turn
            System.out.println("Divine Light used!");
        }));

        // --- 2. FIREBALL (Early Magic) ---
        hero.addSkill(new Skill("Fireball", "Deals fire dmg & inflicts burn.", "Icons/Fireball.png",
            3, 15, Skill.SkillType.ACTIVE, (user, target) -> {
            int dmg = (int) (user.getStats().getAttackPower() * 1.2);
            target.takeDamage(dmg, false);
            target.setBurn(3);
        }));

        // --- 3. FIRESTORM (AOE Magic) ---
        hero.addSkill(new Skill("Firestorm", "High fire dmg to all & burn.", "Icons/Firestorm.png",
            12, 50, Skill.SkillType.ACTIVE, (user, target) -> {
            int dmg = (int) (user.getStats().getAttackPower() * 2.5);
            target.takeDamage(dmg, false);
            target.setBurn(3);
        }));

        // --- 4. FREEZING TOUCH (Control) ---
        hero.addSkill(new Skill("Freezing Touch", "Ice dmg & freezes enemy (Skip Turn).", "Icons/IceTouch.png",
            5, 30, Skill.SkillType.ACTIVE, (user, target) -> {
            int dmg = (int) (user.getStats().getAttackPower() * 1.5);
            target.takeDamage(dmg, false);
            target.setStunned(true);
        }));

        // --- 5. BLIZZARD (AOE Control) ---
        hero.addSkill(new Skill("Blizzard", "Massive ice dmg & slow.", "Icons/Blizzard.png",
            15, 60, Skill.SkillType.ACTIVE, (user, target) -> {
            int dmg = (int) (user.getStats().getAttackPower() * 2.2);
            target.takeDamage(dmg, false);
            target.setSlowDebuff(3);
        }));

        // --- 6. BUCKLE UP (Defense Buff) ---
        hero.addSkill(new Skill("Buckle Up", "Reduce dmg taken by 45%.", "Icons/ShieldBuff.png",
            4, 25, Skill.SkillType.ACTIVE, (user, target) -> {
            user.setDefenseBuff(2);
            System.out.println("Defense Up!");
        }));

        // --- 7. ENDURE (Passive) ---
        hero.addSkill(new Skill("Endure", "Survive a fatal hit with 1 HP.", "Icons/Endure.png",
            10, 0, Skill.SkillType.PASSIVE, (user, target) -> {
            user.setHasEndure(true);
        }));

        // --- 8. DODGE (Speed Buff) ---
        hero.addSkill(new Skill("Dodge", "Increase speed for this turn.", "Icons/Dodge.png",
            2, 10, Skill.SkillType.ACTIVE, (user, target) -> {
            user.setSpeedBuff(1);
        }));

        // --- 9. VISUAL CALCULUS (Sure Hit) ---
        hero.addSkill(new Skill("Visual Calculus", "Next attack will surely hit.", "Icons/Eye.png",
            7, 35, Skill.SkillType.ACTIVE, (user, target) -> {
            user.setNextHitGuaranteed(true);
        }));

        // --- 10. MULTI SLICE (HP Cost) ---
        hero.addSkill(new Skill("Multi Slice", "Costs HP. 3x Hits & Bleed.", "Icons/MultiSlice.png",
            6, 0, Skill.SkillType.ACTIVE, (user, target) -> {
            // Cost 50 HP
            user.takeDamage(50, false);

            int dmgPerHit = (int) (user.getStats().getAttackPower() * 0.4);
            target.takeDamage(dmgPerHit, false);
            target.takeDamage(dmgPerHit, false);
            target.takeDamage(dmgPerHit, false);

            target.setBleed(3);
        }));

        // --- 11. MANA BURST (Original) ---
        hero.addSkill(new Skill("Mana Burst", "Deal 150% ATK Damage.", "Icons/Skill_Burst.png",
            1, 30, Skill.SkillType.ACTIVE, (user, target) -> {
            int dmg = (int) (user.getStats().getAttackPower() * 1.5);
            target.takeDamage(dmg, false);
        }));

        // Update status unlock setelah semua ditambahkan
        hero.checkUnlockSkills();
    }
}
