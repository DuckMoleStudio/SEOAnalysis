public class ClassifiableText { // representation of a search request from XLS file
    String text; // request text
    String classification; // brand, info, commercial...
    double quality; // request quality evaluation, input for training or output for processing

    public ClassifiableText(String text, String classification, double quality) {
        this.text = text;
        this.classification = classification;
        this.quality = quality;
    }

    public String getText() {
        return text;
    }

    public String getClassification() {
        return classification;
    }

    public double getQuality() {
        return quality;
    }
}
