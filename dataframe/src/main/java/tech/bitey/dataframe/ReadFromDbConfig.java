/*
 * Copyright 2021 biteytech@protonmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.bitey.dataframe;

import static tech.bitey.dataframe.DfPreconditions.checkArgument;
import static tech.bitey.dataframe.DfPreconditions.checkState;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.TypedMetaBean;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutableDefaults;
import org.joda.beans.gen.ImmutableValidator;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.MinimalMetaBean;

import tech.bitey.dataframe.db.IFromResultSet;

/**
 * Configuration for reading a dataframe from a {@link ResultSet}. The two
 * configurable settings are:
 * <ul>
 * <li>Mandatory per-column logic for getting data from a {@code ResultSet}. See
 * {@link IFromResultSet} for details.
 * <li>Optional fetch size - passed to {@link ResultSet#setFetchSize(int)}.
 * Default {@code 1000}.
 * </ul>
 * 
 * @author biteytech@protonmail.com
 * 
 * @see DataFrameFactory#readFrom(ResultSet, ReadFromDbConfig)
 * @see IFromResultSet
 */
@BeanDefinition(style = "minimal")
public final class ReadFromDbConfig implements ImmutableBean {

    @PropertyDefinition(validate = "notEmpty")
    private final List<IFromResultSet<?, ?>> fromRsLogic;

    @PropertyDefinition
    private final int fetchSize;

    @ImmutableDefaults
    private static void applyDefaults(Builder builder) {
        builder.fetchSize(1000);
    }

    @ImmutableValidator
    private void validate() {
        checkArgument(fetchSize >= 1, "fetch size must be strictly positive");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    DataFrame read(ResultSet rs) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        final int cc = metaData.getColumnCount();

        checkState(cc == fromRsLogic.size(), "mismatched column count");

        ColumnBuilder[] builders = new ColumnBuilder[cc];
        String[] columnNames = new String[cc];
        IFromResultSet[] fromRsLogic = new IFromResultSet[cc];

        for (int i = 0; i < cc; i++) {
            columnNames[i] = metaData.getColumnName(i + 1);
            fromRsLogic[i] = this.fromRsLogic.get(i);
            builders[i] = fromRsLogic[i].getColumnType().builder();
        }

        rs.setFetchSize(fetchSize);

        while (rs.next())
            for (int i = 0; i < cc; i++)
                fromRsLogic[i].get(rs, i + 1, builders[i]);

        Column<?>[] columns = new Column<?>[cc];
        for (int i = 0; i < columns.length; i++)
            columns[i] = builders[i].build();

        return DataFrameFactory.create(columns, columnNames);
    }

    //------------------------- AUTOGENERATED START -------------------------
    /**
     * The meta-bean for {@code ReadFromDbConfig}.
     */
    private static final TypedMetaBean<ReadFromDbConfig> META_BEAN =
            MinimalMetaBean.of(
                    ReadFromDbConfig.class,
                    new String[] {
                            "fromRsLogic",
                            "fetchSize"},
                    () -> new ReadFromDbConfig.Builder(),
                    b -> b.getFromRsLogic(),
                    b -> b.getFetchSize());

    /**
     * The meta-bean for {@code ReadFromDbConfig}.
     * @return the meta-bean, not null
     */
    public static TypedMetaBean<ReadFromDbConfig> meta() {
        return META_BEAN;
    }

    static {
        MetaBean.register(META_BEAN);
    }

    /**
     * Returns a builder used to create an instance of the bean.
     * @return the builder, not null
     */
    public static ReadFromDbConfig.Builder builder() {
        return new ReadFromDbConfig.Builder();
    }

    private ReadFromDbConfig(
            List<IFromResultSet<?, ?>> fromRsLogic,
            int fetchSize) {
        JodaBeanUtils.notEmpty(fromRsLogic, "fromRsLogic");
        this.fromRsLogic = Collections.unmodifiableList(new ArrayList<>(fromRsLogic));
        this.fetchSize = fetchSize;
        validate();
    }

    @Override
    public TypedMetaBean<ReadFromDbConfig> metaBean() {
        return META_BEAN;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the fromRsLogic.
     * @return the value of the property, not empty
     */
    public List<IFromResultSet<?, ?>> getFromRsLogic() {
        return fromRsLogic;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the fetchSize.
     * @return the value of the property
     */
    public int getFetchSize() {
        return fetchSize;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a builder that allows this bean to be mutated.
     * @return the mutable builder, not null
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            ReadFromDbConfig other = (ReadFromDbConfig) obj;
            return JodaBeanUtils.equal(fromRsLogic, other.fromRsLogic) &&
                    (fetchSize == other.fetchSize);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        hash = hash * 31 + JodaBeanUtils.hashCode(fromRsLogic);
        hash = hash * 31 + JodaBeanUtils.hashCode(fetchSize);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(96);
        buf.append("ReadFromDbConfig{");
        buf.append("fromRsLogic").append('=').append(fromRsLogic).append(',').append(' ');
        buf.append("fetchSize").append('=').append(JodaBeanUtils.toString(fetchSize));
        buf.append('}');
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * The bean-builder for {@code ReadFromDbConfig}.
     */
    public static final class Builder extends DirectFieldsBeanBuilder<ReadFromDbConfig> {

        private List<IFromResultSet<?, ?>> fromRsLogic = Collections.emptyList();
        private int fetchSize;

        /**
         * Restricted constructor.
         */
        private Builder() {
            applyDefaults(this);
        }

        /**
         * Restricted copy constructor.
         * @param beanToCopy  the bean to copy from, not null
         */
        private Builder(ReadFromDbConfig beanToCopy) {
            this.fromRsLogic = new ArrayList<>(beanToCopy.getFromRsLogic());
            this.fetchSize = beanToCopy.getFetchSize();
        }

        //-----------------------------------------------------------------------
        @Override
        public Object get(String propertyName) {
            switch (propertyName.hashCode()) {
                case 1522474643:  // fromRsLogic
                    return fromRsLogic;
                case -1237422629:  // fetchSize
                    return fetchSize;
                default:
                    throw new NoSuchElementException("Unknown property: " + propertyName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Builder set(String propertyName, Object newValue) {
            switch (propertyName.hashCode()) {
                case 1522474643:  // fromRsLogic
                    this.fromRsLogic = (List<IFromResultSet<?, ?>>) newValue;
                    break;
                case -1237422629:  // fetchSize
                    this.fetchSize = (Integer) newValue;
                    break;
                default:
                    throw new NoSuchElementException("Unknown property: " + propertyName);
            }
            return this;
        }

        @Override
        public Builder set(MetaProperty<?> property, Object value) {
            super.set(property, value);
            return this;
        }

        @Override
        public ReadFromDbConfig build() {
            return new ReadFromDbConfig(
                    fromRsLogic,
                    fetchSize);
        }

        //-----------------------------------------------------------------------
        /**
         * Sets the fromRsLogic.
         * @param fromRsLogic  the new value, not empty
         * @return this, for chaining, not null
         */
        public Builder fromRsLogic(List<IFromResultSet<?, ?>> fromRsLogic) {
            JodaBeanUtils.notEmpty(fromRsLogic, "fromRsLogic");
            this.fromRsLogic = fromRsLogic;
            return this;
        }

        /**
         * Sets the {@code fromRsLogic} property in the builder
         * from an array of objects.
         * @param fromRsLogic  the new value, not empty
         * @return this, for chaining, not null
         */
        @SafeVarargs
        public final Builder fromRsLogic(IFromResultSet<?, ?>... fromRsLogic) {
            return fromRsLogic(Arrays.asList(fromRsLogic));
        }

        /**
         * Sets the fetchSize.
         * @param fetchSize  the new value
         * @return this, for chaining, not null
         */
        public Builder fetchSize(int fetchSize) {
            this.fetchSize = fetchSize;
            return this;
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder(96);
            buf.append("ReadFromDbConfig.Builder{");
            buf.append("fromRsLogic").append('=').append(JodaBeanUtils.toString(fromRsLogic)).append(',').append(' ');
            buf.append("fetchSize").append('=').append(JodaBeanUtils.toString(fetchSize));
            buf.append('}');
            return buf.toString();
        }

    }

    //-------------------------- AUTOGENERATED END --------------------------
}
