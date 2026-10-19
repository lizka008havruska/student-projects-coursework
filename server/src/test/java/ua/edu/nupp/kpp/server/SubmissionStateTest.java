package ua.edu.nupp.kpp.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SubmissionStateTest {
    @Test
    void allowsOnlySequentialTransitions() {
        assertTrue(SubmissionState.DRAFT.canMoveTo(SubmissionState.SUBMITTED));
        assertTrue(SubmissionState.SUBMITTED.canMoveTo(SubmissionState.REVIEW));
        assertTrue(SubmissionState.REVIEW.canMoveTo(SubmissionState.ACCEPTED));
        assertFalse(SubmissionState.DRAFT.canMoveTo(SubmissionState.ACCEPTED));
        assertFalse(SubmissionState.ACCEPTED.canMoveTo(SubmissionState.DRAFT));
    }
}
