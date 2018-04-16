package snail.common.values;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A string value.
 */
public final class SnailString extends SnailSimple {
    private final String value;

    public SnailString(@Nonnull String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public SnailValueType valueType() {
        return SnailValueType.T_string;
    }

    @Override
    public String asPrintable() {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public String value() {
        return value;
    }
}
