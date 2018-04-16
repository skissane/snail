package snail.common.values;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * An object.
 */
public final class SnailObject extends SnailComplex {
    private final SnailRef ofClass;
    private final NavigableMap<SnailRef, SnailHeapValue> attrs = new TreeMap<>();
    private NavigableSet<SnailRef> attrRefs = Collections.unmodifiableNavigableSet(attrs.navigableKeySet());

    public SnailObject(@Nonnull SnailRef ofClass,
                       @Nonnull SnailComplexParent parent) {
        super(parent);
        this.ofClass = Objects.requireNonNull(ofClass);
    }

    @Override
    public SnailValueType valueType() {
        return SnailValueType.T_object;
    }

    @Override
    public String asPrintable() {
        final StringBuilder r = new StringBuilder();
        r.append("[");
        r.append(ofClass);
        attrs.forEach((attr, value) -> {
            r.append(" ");
            r.append(attr.getNameRelativeTo(ofClass));
            r.append(" ");
            r.append(SnailValue.asPrintable(value));
        });
        r.append("]");
        return r.toString();
    }

    public SnailRef ofClass() {
        return ofClass;
    }

    public NavigableSet<SnailRef> attrs() {
        return attrRefs;
    }

    public SnailHeapValue get(@Nonnull SnailRef attr) {
        return attrs.get(attr);
    }

    public static SnailObject make(@Nonnull SnailRef ofClass, @Nullable SnailComplex parent) {
        if (parent == null) {
            final SnailDocument doc = new SnailDocument();
            final SnailObject object = new SnailObject(ofClass, doc);
            doc.root(object);
            return object;
        }
        return new SnailObject(ofClass, parent);
    }

    @Override
    protected SnailComplex copy(@Nonnull SnailComplexParent newParent) {
        final SnailObject obj = new SnailObject(ofClass, newParent);
        attrs.forEach((attr, value) -> obj.attrs.put(attr, SnailValue.copy(value, newParent)));
        return obj;
    }

    public void set(@Nonnull SnailRef attr, @Nullable SnailHeapValue value) {
        final SnailHeapValue existing = attrs.get(attr);
        if (existing != null)
            remove(attr);
        if (value == null || value instanceof SnailSimple)
            attrs.put(attr, value);
        else if (value instanceof SnailComplex) {
            SnailComplex v = (SnailComplex) value;
            if (v.parent() != this)
                v = v.copyNonRoot(() -> this);
            v.parent(this);
            attrs.put(attr, value);
        }
    }

    private void remove(@Nonnull SnailRef attr) {
        final SnailHeapValue existing = attrs.remove(attr);
        SnailComplex.deleteIfComplex(existing);
    }

    @Override
    protected void doDelete() {
        while (true) {
            final SnailRef attr = attrRefs.pollFirst();
            if (attr == null)
                break;
            remove(attr);
        }
    }
}
