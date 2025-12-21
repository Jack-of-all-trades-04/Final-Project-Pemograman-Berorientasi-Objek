package com.FEA_3.frontend.Patterns.Command;

import com.FEA_3.frontend.Entity.GameUnit;
import com.FEA_3.frontend.Entity.Skill;

public class SkillCommand implements Command {
    private GameUnit user;
    private GameUnit target;
    private Skill skill;

    public SkillCommand(GameUnit user, GameUnit target, Skill skill) {
        this.user = user;
        this.target = target;
        this.skill = skill;
    }

    @Override
    public void execute() {
        user.getStats().consumeMana(skill.getManaCost());
        skill.use(user, target);
    }

    @Override
    public String getDescription() {
        return user.getName() + " uses " + skill.getName() + "!";
    }
}
