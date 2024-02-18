package sit.tuvarna.bg.core.externalservices;

import org.springframework.stereotype.Service;

@Service
public class XPProgressService {
    public XPProgress calculateXPProgress(int totalXp) {
        return new XPProgress(totalXp);
    }
}
