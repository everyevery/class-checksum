package net.everyevery;

public enum DigestAlgorithm {
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256");

    private String digestAlgotirhm;

    DigestAlgorithm(String digestAlgotirhm) {
        this.digestAlgotirhm = digestAlgotirhm;
    }

    @Override
    public String toString() {
        return this.digestAlgotirhm;
    }
}
