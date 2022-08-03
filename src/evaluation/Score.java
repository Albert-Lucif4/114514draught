package evaluation;

class Score {
    private int mg_score;
    private int eg_score;

    Score() {
        mg_score = 0;
        eg_score = 0;
    }

    Score(int mg_score, int eg_score) {
        this.mg_score = mg_score;
        this.eg_score = eg_score;
    }

    void add(int mg_bonus, int eg_bonus) {
        this.mg_score += mg_bonus;
        this.eg_score += eg_bonus;
    }

    void add(int mg_bonus, int eg_bonus, float factor) {
        this.mg_score += mg_bonus * factor;
        this.eg_score += eg_bonus * factor;
    }

    void add(int mg_bonus, int eg_bonus, int factor) {
        this.mg_score += mg_bonus * factor;
        this.eg_score += eg_bonus * factor;
    }

    void add(Score score) {
        this.mg_score += score.mg_score;
        this.eg_score += score.eg_score;
    }

    void minus(int mg_bonus, int eg_bonus) {
        this.mg_score -= mg_bonus;
        this.eg_score -= eg_bonus;
    }

    void minus(int mg_bonus, int eg_bonus, float factor) {
        this.mg_score -= mg_bonus * factor;
        this.eg_score -= eg_bonus * factor;
    }

    void minus(int mg_bonus, int eg_bonus, int factor) {
        this.mg_score -= mg_bonus * factor;
        this.eg_score -= eg_bonus * factor;
    }

    void minus(Score score) {
        this.mg_score -= score.mg_score;
        this.eg_score -= score.eg_score;
    }

    void setMg(int score) {
        this.mg_score = score;
    }

    void setEg(int score) {
        this.eg_score = score;
    }

    int MgScore() {
        return mg_score;
    }

    int EgScore() {
        return eg_score;
    }

    void show() {
        System.out.println("MG: " + mg_score + ", EG: " + eg_score);
    }
}
