package com.nageshwarsaini.dynamic.workflows.activity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nageshwarsaini.dynamic.workflows.steps.api.IStepExecutor;
import io.temporal.common.converter.EncodedValues;
import io.temporal.testing.TestActivityEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.temporal.activity.DynamicActivity;
import java.util.Collections;
import java.util.Optional;
import io.temporal.activity.Activity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DynamicActivityImplTest {

    private TestActivityEnvironment testEnvironment;
    private DynamicActivity activity;
    private IStepExecutor mockExecutor;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        testEnvironment = TestActivityEnvironment.newInstance();
        mockExecutor = mock(IStepExecutor.class);
        when(mockExecutor.getType()).thenReturn("MOCK_TYPE");

        testEnvironment.registerActivitiesImplementations(new DynamicActivityImpl(Collections.singletonList(mockExecutor)));
        activity = testEnvironment.newActivityStub(DynamicActivity.class);
        mapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }
}
