package edu.mriu.kankhol;

/**
 * Created by meetesh on 09/01/18.
 */

/**
 * Helper Class
 */

public class SessionState {
    private String opt1, opt2, opt3;
    private String subject;
    private float timeTakenInMs;
    private boolean result;
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {return id; }

    public String getOpt1() {
        return opt1;
    }

    public void setOpt1(String opt1) {
        this.opt1 = opt1;
    }

    public String getOpt2() {
        return opt2;
    }

    public void setOpt2(String opt2) {
        this.opt2 = opt2;
    }

    public String getOpt3() {
        return opt3;
    }

    public void setOpt3(String opt3) {
        this.opt3 = opt3;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public float getTimeTakenInMs() {
        return timeTakenInMs;
    }

    public void setTimeTakenInMs(float timeTakenInMs) {
        this.timeTakenInMs = timeTakenInMs;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public SessionState(String id, String opt1, String opt2, String opt3, String testingFor) {
        setId(id);
        setOpt1(opt1);
        setOpt2(opt2);
        setOpt3(opt3);
        setSubject(testingFor);
    }

    public SessionState() {}
}
