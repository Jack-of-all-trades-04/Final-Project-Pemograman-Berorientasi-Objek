package com.FEA_3.frontend.Patterns.Command;
import com.FEA_3.frontend.Entity.GameUnit;

public class DefendCommand implements Command {
    private GameUnit self;

    public DefendCommand(GameUnit self) {
        this.self = self;
    }

    @Override
    public void execute() {
        self.setDefending(true);
        System.out.println(self.getName() + " bersiap menahan serangan!");
        // Bisa tambahkan animasi/state: self.setState(UnitState.DEFEND);
    }
}
