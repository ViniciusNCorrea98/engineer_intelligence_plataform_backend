package io.eip.ingestion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvidencePersistence {

    private final IngestedEvidenceRepository evidences;

    public EvidencePersistence(IngestedEvidenceRepository e) {
        evidences = e;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IngestedEvidence insert(IngestedEvidence evidence) {
        return evidences.saveAndFlush(evidence);
    }
}
