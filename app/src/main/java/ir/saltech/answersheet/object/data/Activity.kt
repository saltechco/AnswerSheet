package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.Date;

public class Activity extends Thing {
    private String message;
    private Date submitDate;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Activity{" +
                "message='" + message + '\'' +
                ", submitDate=" + submitDate +
                '}';
    }
}
