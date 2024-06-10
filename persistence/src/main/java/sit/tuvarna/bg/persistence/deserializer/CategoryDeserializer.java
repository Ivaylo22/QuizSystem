package sit.tuvarna.bg.persistence.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sit.tuvarna.bg.persistence.entity.Category;
import sit.tuvarna.bg.persistence.repository.CategoryRepository;

import java.io.IOException;

@Component
public class CategoryDeserializer extends JsonDeserializer<Category> {

    private static final Logger logger = LoggerFactory.getLogger(CategoryDeserializer.class);
    private static CategoryRepository categoryRepository;

    @Autowired
    public void setCategoryRepository(CategoryRepository repository) {
        categoryRepository = repository;
    }

    @Override
    public Category deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        String categoryName = node.asText();
        return categoryRepository.findByCategory(categoryName)
                .orElseGet(() -> {
                    logger.warn("Category not found: {}. Creating a new one.", categoryName);
                    Category newCategory = Category.builder().category(categoryName).build();
                    return categoryRepository.save(newCategory);
                });
    }
}