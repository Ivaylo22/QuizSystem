package sit.tuvarna.bg.persistence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import sit.tuvarna.bg.persistence.entity.Category;

import java.io.IOException;

public class CategorySerializer extends StdSerializer<Category> {

    public CategorySerializer() {
        this(null);
    }

    public CategorySerializer(Class<Category> t) {
        super(t);
    }

    @Override
    public void serialize(Category category, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(category.getCategory());
    }
}
