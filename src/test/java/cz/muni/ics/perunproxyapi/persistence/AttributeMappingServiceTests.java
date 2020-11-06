package cz.muni.ics.perunproxyapi.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AttributeMappingServiceTests {

    private static final String ATTRIBUTES_PATH = "src/test/resources/configs/attributes.yml";
    private static final String ALTERNATIVE_ATTRIBUTES_PATH = "src/test/resources/configs/alternative_attributes.yml";
    private static final List<String> PATHS = new ArrayList<>(
            Arrays.asList(ATTRIBUTES_PATH, ALTERNATIVE_ATTRIBUTES_PATH));

    private static final String IS_TEST_SP_ID = "urn:perun:facility:attribute-def:def:isTestSp";

    @Autowired
    private AttributeMappingService attributeMappingService;

    @BeforeEach
    public void setUp(){
        clearAttributeMap();
    }

    private void clearAttributeMap() {
        attributeMappingService.getAttributeMap().clear();
    }

    // Tests

    @Test
    @Order(1)
    // In the first test, we want to check if all paths were loaded correctly from the configuration during autowiring.
    // This test must always be FIRST.
    public void testLoadAllAttributesFromAllPaths() {
        assertEquals(2, attributeMappingService.getPaths().size());
        attributeMappingService.postInit();
        assertEquals(9, attributeMappingService.getAttributeMap().size());
    }

    @Test
    public void testLoadSingleAttributeFile() {
        ReflectionTestUtils.setField(attributeMappingService, "paths", Collections.singletonList(ATTRIBUTES_PATH));
        attributeMappingService.postInit();
        assertEquals(8, attributeMappingService.getAttributeMap().size());

        this.clearAttributeMap();

        ReflectionTestUtils.setField(attributeMappingService, "paths", Collections.singletonList(ALTERNATIVE_ATTRIBUTES_PATH));
        attributeMappingService.postInit();
        assertEquals(2 , attributeMappingService.getAttributeMap().size());
    }

    @Test
    public void testEmptyAttributeFile() {
        ReflectionTestUtils.setField(attributeMappingService, "paths", new ArrayList<>());
        attributeMappingService.postInit();

        assertEquals(0, attributeMappingService.getPaths().size());
        assertEquals(0, attributeMappingService.getAttributeMap().size());
    }

    @Test
    public void testOverwriteValueInMap() {
        ReflectionTestUtils.setField(attributeMappingService, "paths", Collections.singletonList(ATTRIBUTES_PATH));
        attributeMappingService.postInit();
        assertEquals("isTestSp", attributeMappingService.getAttributeMap().get(IS_TEST_SP_ID).getLdapName());

        ReflectionTestUtils.setField(attributeMappingService, "paths", Collections.singletonList(ALTERNATIVE_ATTRIBUTES_PATH));
        attributeMappingService.postInit();
        assertEquals("isTestSpAlternative", attributeMappingService.getAttributeMap().get(IS_TEST_SP_ID).getLdapName());
    }

}
