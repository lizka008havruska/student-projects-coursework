package ua.edu.nupp.kpp.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LifecycleTest {
    @Test
    void fullForwardChainIsAllowed() {
        assertTrue(SubmissionService.isAllowedTransition(SubmissionState.DRAFT, SubmissionState.SUBMITTED));
        assertTrue(SubmissionService.isAllowedTransition(SubmissionState.SUBMITTED, SubmissionState.REVIEW));
        assertTrue(SubmissionService.isAllowedTransition(SubmissionState.REVIEW, SubmissionState.ACCEPTED));
    }

    @Test
    void skippingStatesIsForbidden() {
        assertFalse(SubmissionService.isAllowedTransition(SubmissionState.DRAFT, SubmissionState.REVIEW));
        assertFalse(SubmissionService.isAllowedTransition(SubmissionState.DRAFT, SubmissionState.ACCEPTED));
    }

    @Test
    void backwardAndSameStateAreForbidden() {
        assertFalse(SubmissionService.isAllowedTransition(SubmissionState.REVIEW, SubmissionState.SUBMITTED));
        assertFalse(SubmissionService.isAllowedTransition(SubmissionState.ACCEPTED, SubmissionState.DRAFT));
        assertFalse(SubmissionService.isAllowedTransition(SubmissionState.DRAFT, SubmissionState.DRAFT));
    }

    @Test
    void nullTransitionIsForbidden() {
        assertFalse(SubmissionService.isAllowedTransition(SubmissionState.DRAFT, null));
        assertFalse(SubmissionService.isAllowedTransition(null, SubmissionState.SUBMITTED));
    }
}
