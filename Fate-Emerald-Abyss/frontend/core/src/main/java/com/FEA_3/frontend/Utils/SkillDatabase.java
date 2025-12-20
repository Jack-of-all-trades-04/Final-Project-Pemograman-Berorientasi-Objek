package com.FEA_3.frontend.Utils;

import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.EnemyType;
import com.FEA_3.frontend.Entity.Skill;
import com.badlogic.gdx.audio.Sound;

public class SkillDatabase {

    /**
     * Method ini khusus memuat skill set untuk class SABER (Artoria)
     */
    public static void loadSaberSkills(GameUnit hero) {

        // --- 1. DIVINE LIGHT (Support) ---
        hero.addSkill(new Skill("Divine Light", "Heal 15% HP & lower enemy accuracy.", "Icons/DivineLight.png",
            2, 20, Skill.SkillType.ACTIVE, (user, target) -> {
            ResourceManager.getInstance().getSound("Audio/Sound_Effect/Divine Light.mp3").play();
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
            ResourceManager.getInstance().getSound("Audio/Sound_Effect/Fireball.mp3").play();
            int dmg = (int) (user.getStats().getAttackPower() * 1.2);
            target.takeDamage(dmg, false);
            target.setBurn(3);
        }));

        // --- 3. FIRESTORM (AOE Magic) ---
        hero.addSkill(new Skill("Firestorm", "High fire dmg to all & burn.", "Icons/Firestorm.png",
            12, 50, Skill.SkillType.ACTIVE, (user, target) -> {
            ResourceManager.getInstance().getSound("Audio/Sound_Effect/Firestorm.mp3").play();
            int dmg = (int) (user.getStats().getAttackPower() * 2.5);
            target.takeDamage(dmg, false);
            target.setBurn(3);
        }));

        // --- 4. FREEZING TOUCH (Control) ---
        hero.addSkill(new Skill("Freezing Touch", "Ice dmg & freezes enemy (Skip Turn).", "Icons/IceTouch.png",
            5, 30, Skill.SkillType.ACTIVE, (user, target) -> {
            ResourceManager.getInstance().getSound("Audio/Sound_Effect/Freezing Touch.mp3").play();
            int dmg = (int) (user.getStats().getAttackPower() * 1.5);
            target.takeDamage(dmg, false);
            target.setStunned(true);
        }));

        // --- 5. BLIZZARD (AOE Control) ---
        hero.addSkill(new Skill("Blizzard", "Massive ice dmg & slow.", "Icons/Blizzard.png",
            15, 60, Skill.SkillType.ACTIVE, (user, target) -> {
            Sound blizzardSfx = ResourceManager.getInstance().getSound("Audio/Sound_Effect/Blizzard (Harus diperpendek).mp3");
            long id = blizzardSfx.play(0.1f); // Mulai dari volume rendah (Fade In awal)

            // --- Simulasi Fade In ---
            for(float i = 0.2f; i <= 1.0f; i += 0.2f) {
                final float vol = i;
                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override public void run() { blizzardSfx.setVolume(id, vol); }
                }, vol * 0.5f); // Naik bertahap setiap 0.5 detik
            }

            // --- Simulasi Fade Out (setelah 3 detik suara berjalan) ---
            com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                @Override
                public void run() {
                    for(int j = 0; j < 10; j++) {
                        final float fadeVol = 1.0f - (j * 0.1f);
                        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                            @Override public void run() {
                                blizzardSfx.setVolume(id, fadeVol);
                                if(fadeVol <= 0.1f) blizzardSfx.stop(id); // Stop saat sudah sunyi
                            }
                        }, j * 0.1f);
                    }
                }
            }, 3.0f);
            int dmg = (int) (user.getStats().getAttackPower() * 2.2);
            target.takeDamage(dmg, false);
            target.setSlowDebuff(3);
        }));

        // --- 6. BUCKLE UP (Defense Buff) ---
        hero.addSkill(new Skill("Buckle Up", "Reduce dmg taken by 45%.", "Icons/ShieldBuff.png",
            4, 25, Skill.SkillType.ACTIVE, (user, target) -> {
            ResourceManager.getInstance().getSound("Audio/Sound_Effect/Buckle Up (maybe).mp3").play();
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
            ResourceManager.getInstance().getSound("Audio/Sound_Effect/Dodge.mp3").play();
            user.setSpeedBuff(1);
        }));

        // --- 9. VISUAL CALCULUS (Sure Hit) ---
        hero.addSkill(new Skill("Visual Calculus", "Next attack will surely hit.", "Icons/Eye.png",
            7, 35, Skill.SkillType.ACTIVE, (user, target) -> {
            ResourceManager.getInstance().getSound("Audio/Sound_Effect/Visual Calculus.mp3").play();
            user.setNextHitGuaranteed(true);
        }));

        // --- 10. MULTI SLICE (HP Cost) ---
        hero.addSkill(new Skill("Multi Slice", "Costs HP. 3x Hits & Bleed.", "Icons/MultiSlice.png",
            6, 0, Skill.SkillType.ACTIVE, (user, target) -> {
            Sound sliceSfx = ResourceManager.getInstance().getSound("Audio/Sound_Effect/Multi Slice (Efek hanya satu slice).mp3");
            sliceSfx.play();
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

    public static void loadEnemySkills(GameUnit enemy, EnemyType type) {
        switch (type) {
            case ASSASSIN: // Skill: Execution Mark
                enemy.addSkill(new Skill("Execution Mark", "Mark target for death.", "Icons/Mark.png", 0, 20, Skill.SkillType.ACTIVE, (user, target) -> {
                    System.out.println("Assassin used Execution Mark!");
                    target.setDefenseBuff(-2); // Debuff Def (Logic sederhana)
                }));
                break;

            case LANCER: // Skill: Impale Drive (ATK x 1.2)
                enemy.addSkill(new Skill("Impale Drive", "Piercing attack.", "Icons/Spear.png", 0, 30, Skill.SkillType.ACTIVE, (user, target) -> {
                    int dmg = (int) (user.getStats().getAttackPower() * 1.2);
                    target.takeDamage(dmg, false);
                }));
                break;

            case ANOMIMUS: // Skill: Through (ATK x 1.2, Ignore 30% Def)
                enemy.addSkill(new Skill("Through", "Piercing damage.", "Icons/Pierce.png", 0, 10, Skill.SkillType.ACTIVE, (user, target) -> {
                    // Simulasi ignore def di logic damage nanti
                    int dmg = (int) (user.getStats().getAttackPower() * 1.2);
                    target.takeDamage(dmg, false);
                }));
                break;

            case BEELING: // Skill: Sting (ATK x 1.1 + Poison/Bleed)
                enemy.addSkill(new Skill("Sting", "Poisonous attack.", "Icons/Sting.png", 0, 5, Skill.SkillType.ACTIVE, (user, target) -> {
                    int dmg = (int) (user.getStats().getAttackPower() * 1.1);
                    target.takeDamage(dmg, false);
                    target.setBleed(3); // Anggap Poison = Bleed di sistem kita
                }));
                break;

            case SLIME: // Skill: Gulp (Heal 15% Max HP)
                enemy.addSkill(new Skill("Gulp", "Heals self.", "Icons/Potion.png", 0, 10, Skill.SkillType.ACTIVE, (user, target) -> {
                    int heal = (int) (user.getStats().getMaxHp() * 0.15);
                    int newHp = user.getStats().getCurrentHp() + heal;
                    if(newHp > user.getStats().getMaxHp()) newHp = user.getStats().getMaxHp();
                    user.getStats().setCurrentHp(newHp);
                    System.out.println("Slime used Gulp!");
                }));
                break;

            case GOLEM: // Skill: Shockwave (Stun 1 Turn)
                enemy.addSkill(new Skill("Shockwave", "Stuns enemy.", "Icons/Stun.png", 0, 20, Skill.SkillType.ACTIVE, (user, target) -> {
                    int dmg = user.getStats().getAttackPower();
                    target.takeDamage(dmg, false);
                    target.setStunned(true);
                }));
                break;

            case RIDER: // Skill: Trample Rush (ATK x 1.1 + Stun Chance?)
                enemy.addSkill(new Skill("Trample Rush", "Charge attack.", "Icons/Charge.png", 0, 15, Skill.SkillType.ACTIVE, (user, target) -> {
                    int dmg = (int) (user.getStats().getAttackPower() * 1.1);
                    target.takeDamage(dmg, false);
                    // 30% Chance Stun
                    if (Math.random() < 0.3) {
                        target.setStunned(true);
                        System.out.println("Rider stunned the target!");
                    }
                }));
                break;

            case PRETENDER: // Skill: False Command (Debuff Accuracy / Confusion)
                enemy.addSkill(new Skill("False Command", "Lowers accuracy.", "Icons/Debuff.png", 0, 25, Skill.SkillType.ACTIVE, (user, target) -> {
                    target.setAccuracyDebuff(3); // -25% Acc selama 3 turn
                    System.out.println("Pretender used False Command!");
                }));
                break;

            case FARHAT: // Boss Forest: Storms Arrow (ATK x 1.3 + Slow)
                enemy.addSkill(new Skill("Storms Arrow", "High Dmg + Slow.", "Icons/Arrow.png", 0, 40, Skill.SkillType.ACTIVE, (user, target) -> {
                    int dmg = (int) (user.getStats().getAttackPower() * 1.3);
                    target.takeDamage(dmg, false);
                    target.setSlowDebuff(3); // Slow Player
                }));
                break;

            case MANDA: // Boss Ruins: Barbaric Slash (ATK x 1.5 + Bleed)
                enemy.addSkill(new Skill("Barbaric Slash", "Massive Dmg + Bleed.", "Icons/Slash.png", 0, 50, Skill.SkillType.ACTIVE, (user, target) -> {
                    int dmg = (int) (user.getStats().getAttackPower() * 1.5);
                    target.takeDamage(dmg, true); // Force Critical (Sakit banget!)
                    target.setBleed(3);
                }));
                break;
        }

        // Musuh tidak perlu checkUnlockSkills() karena levelnya fix,
        // tapi kita panggil saja biar aktif di list skills
        enemy.checkUnlockSkills();
    }
}
