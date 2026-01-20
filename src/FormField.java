
/**
 * Configuration object described a single input field in a FormPanel.
 */
public class FormField {
    /**
     * Supported input types.
     */
    public enum FieldType {
        TEXT, PASSWORD, NUMBER, DATE
    }

    private String name;
    private String label;
    private FieldType type;
    private String defaultValue;
    private boolean required;
    private String tooltip;
    private int columns;

    /**
     * Creates a new form field configuration.
     * 
     * @param name  Unique identifier for the field.
     * @param label Display label.
     * @param type  Input type (e.g., TEXT, PASSWORD).
     */
    public FormField(String name, String label, FieldType type) {
        this(name, label, type, "", true, "", 20);
    }

    public FormField(String name, String label, FieldType type, String defaultValue) {
        this(name, label, type, defaultValue, true, "", 20);
    }

    public FormField(String name, String label, FieldType type, String defaultValue, boolean required) {
        this(name, label, type, defaultValue, required, "", 20);
    }

    public FormField(String name, String label, FieldType type, String defaultValue, boolean required, String tooltip,
            int columns) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.defaultValue = defaultValue;
        this.required = required;
        this.tooltip = tooltip;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public FieldType getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public String getTooltip() {
        return tooltip;
    }

    public int getColumns() {
        return columns;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
