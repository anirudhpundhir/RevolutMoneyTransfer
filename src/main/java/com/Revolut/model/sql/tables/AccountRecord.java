package com.Revolut.model.sql.tables;

import java.math.BigDecimal;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
        value = {
                "http://www.jooq.org",
                "jOOQ version:3.11.5"
        },
        comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccountRecord extends UpdatableRecordImpl<AccountRecord> implements Record4<Long, Boolean, BigDecimal, String> {

    private static final long serialVersionUID = -1170781460;

    /**
     * Setter for <code>PUBLIC.ACCOUNT.ID</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>PUBLIC.ACCOUNT.ID</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>PUBLIC.ACCOUNT.LOCKED</code>.
     */
    public void setLocked(Boolean value) {
        set(1, value);
    }

    /**
     * Getter for <code>PUBLIC.ACCOUNT.LOCKED</code>.
     */
    public Boolean getLocked() {
        return (Boolean) get(1);
    }

    /**
     * Setter for <code>PUBLIC.ACCOUNT.MONEY_VALUE</code>.
     */
    public void setMoneyValue(BigDecimal value) {
        set(2, value);
    }

    /**
     * Getter for <code>PUBLIC.ACCOUNT.MONEY_VALUE</code>.
     */
    public BigDecimal getMoneyValue() {
        return (BigDecimal) get(2);
    }

    /**
     * Setter for <code>PUBLIC.ACCOUNT.MONEY_CURRENCY</code>.
     */
    public void setMoneyCurrency(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>PUBLIC.ACCOUNT.MONEY_CURRENCY</code>.
     */
    public String getMoneyCurrency() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Long, Boolean, BigDecimal, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Long, Boolean, BigDecimal, String> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Account.ACCOUNT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field2() {
        return Account.ACCOUNT.LOCKED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field3() {
        return Account.ACCOUNT.MONEY_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Account.ACCOUNT.MONEY_CURRENCY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component2() {
        return getLocked();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component3() {
        return getMoneyValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getMoneyCurrency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value2() {
        return getLocked();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value3() {
        return getMoneyValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getMoneyCurrency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountRecord value2(Boolean value) {
        setLocked(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountRecord value3(BigDecimal value) {
        setMoneyValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountRecord value4(String value) {
        setMoneyCurrency(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountRecord values(Long value1, Boolean value2, BigDecimal value3, String value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AccountRecord
     */
    public AccountRecord() {
        super(Account.ACCOUNT);
    }

    /**
     * Create a detached, initialised AccountRecord
     */
    public AccountRecord(Long id, Boolean locked, BigDecimal moneyValue, String moneyCurrency) {
        super(Account.ACCOUNT);

        set(0, id);
        set(1, locked);
        set(2, moneyValue);
        set(3, moneyCurrency);
    }
}