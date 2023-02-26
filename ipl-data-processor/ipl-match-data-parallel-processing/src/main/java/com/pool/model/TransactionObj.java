package com.pool.model;

import com.pool.model.JaxbDateSerializer;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement(name="transaction")
public class TransactionObj {

    private String account;

    private Date timestamp;

    private BigDecimal amount;

    public TransactionObj(String account, Date timestamp, BigDecimal amount) {
        this.account = account;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    @XmlJavaTypeAdapter(JaxbDateSerializer.class)
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
