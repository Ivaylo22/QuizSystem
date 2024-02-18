package sit.tuvarna.bg.core.externalservices;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class XPProgress {
    private static final List<Integer> xpRequirements = List.of(0, 100, 250, 500, 750, 1000, 1250, 1500, 1750, 2000);
    private int totalXp;
    private int level;
    private int xpTowardsNextLevel;
    private int xpRequirementForNextLevel;

    public XPProgress(int totalXp) {
        this.totalXp = totalXp;
        calculateProgress();
    }

    private void calculateProgress() {
        level = 1;
        xpTowardsNextLevel = totalXp - xpRequirements.get(0);
        xpRequirementForNextLevel = xpRequirements.get(1) - xpRequirements.get(0);

        for (int i = 1; i < xpRequirements.size(); i++) {
            if (totalXp < xpRequirements.get(i)) {
                xpTowardsNextLevel = totalXp - xpRequirements.get(i-1);
                xpRequirementForNextLevel = xpRequirements.get(i);
                break;
            } else {
                level = i + 1;
            }
        }
    }
}
