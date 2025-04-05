import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PatternBuilder {
    private final List<PatternComponent> components = new ArrayList<>();

    public PatternBuilder add(PatternComponent component) {
        components.add(component);
        return this;
    }

    public String build() {
        return components.stream()
                .map(PatternComponent::getRegex)
                .collect(Collectors.joining());
    }

    public PatternBuilder joinOR(PatternBuilder other) {
        String thisRegex = this.build();
        String otherRegex = other.build();
        this.components.clear();
        this.components.add(new OrComponent(thisRegex, otherRegex));
        return this;
    }
    public PatternBuilder join(PatternBuilder other) {
        String thisRegex = this.build();
        String otherRegex = other.build();
        this.components.clear();
        this.components.add(new ConcatenateComponent(thisRegex, otherRegex));
        return this;
    }


    @Override
    public String toString() {
        return build();
    }

}
