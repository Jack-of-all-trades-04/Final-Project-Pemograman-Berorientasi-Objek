package com.FEA_3.frontend.Patterns.Strategy;

import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.Skill;
import com.FEA_3.frontend.Patterns.Command.Command; // Import Command
import java.util.List;
import java.util.Random;

public class SmartAIStrategy implements BattleStrategy {
    private Random rng = new Random();

    @Override
    public Command decideAction(GameUnit self, GameUnit target) {
        // 1. Cek HP Rendah (Heal Priority)
        if (self.getStats().getCurrentHp() < self.getStats().getMaxHp() * 0.3) {
            Skill healSkill = findSkillByName(self, "Gulp");
            if (healSkill != null && canCast(self, healSkill)) {
                // Return Command untuk Cast Skill Heal
                return () -> castSkill(self, target, healSkill);
            }
        }

        // 2. Logic Random (70% Skill, 30% Attack)
        if (rng.nextInt(100) < 70) {
            List<Skill> usableSkills = self.getUnlockedSkills().stream()
                .filter(s -> canCast(self, s) && s.getType() == Skill.SkillType.ACTIVE)
                .toList();

            if (!usableSkills.isEmpty()) {
                Skill chosenSkill = usableSkills.get(rng.nextInt(usableSkills.size()));
                // Return Command untuk Cast Skill Serang
                return () -> castSkill(self, target, chosenSkill);
            }
        }

        // 3. Fallback: Return Command untuk Basic Attack
        return () -> {
            System.out.println(self.getName() + " performs Basic Attack.");
            self.attackTarget(target);
        };
    }

    // --- Helper Methods (Tetap Sama) ---

    private boolean canCast(GameUnit user, Skill s) {
        return user.getStats().getCurrentMp() >= s.getManaCost();
    }

    private Skill findSkillByName(GameUnit user, String name) {
        for (Skill s : user.getUnlockedSkills()) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    private void castSkill(GameUnit user, GameUnit target, Skill s) {
        System.out.println(user.getName() + " casts " + s.getName() + "!");
        user.getStats().consumeMana(s.getManaCost());
        s.use(user, target);
    }
}
