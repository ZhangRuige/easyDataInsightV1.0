// ORM class for table 'm_prod_params'
// WARNING: This class is AUTO-GENERATED. Modify at your own risk.
//
// Debug information:
// Generated date: Sat Dec 26 14:31:39 HKT 2015
// For connector: org.apache.sqoop.manager.MySQLManager
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;
import com.cloudera.sqoop.lib.JdbcWritableBridge;
import com.cloudera.sqoop.lib.DelimiterSet;
import com.cloudera.sqoop.lib.FieldFormatter;
import com.cloudera.sqoop.lib.RecordParser;
import com.cloudera.sqoop.lib.BooleanParser;
import com.cloudera.sqoop.lib.BlobRef;
import com.cloudera.sqoop.lib.ClobRef;
import com.cloudera.sqoop.lib.LargeObjectLoader;
import com.cloudera.sqoop.lib.SqoopRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class m_prod_params extends SqoopRecord  implements DBWritable, Writable {
  private final int PROTOCOL_VERSION = 3;
  public int getClassFormatVersion() { return PROTOCOL_VERSION; }
  protected ResultSet __cur_result_set;
  private String prod_id;
  public String get_prod_id() {
    return prod_id;
  }
  public void set_prod_id(String prod_id) {
    this.prod_id = prod_id;
  }
  public m_prod_params with_prod_id(String prod_id) {
    this.prod_id = prod_id;
    return this;
  }
  private String param;
  public String get_param() {
    return param;
  }
  public void set_param(String param) {
    this.param = param;
  }
  public m_prod_params with_param(String param) {
    this.param = param;
    return this;
  }
  private String val;
  public String get_val() {
    return val;
  }
  public void set_val(String val) {
    this.val = val;
  }
  public m_prod_params with_val(String val) {
    this.val = val;
    return this;
  }
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof m_prod_params)) {
      return false;
    }
    m_prod_params that = (m_prod_params) o;
    boolean equal = true;
    equal = equal && (this.prod_id == null ? that.prod_id == null : this.prod_id.equals(that.prod_id));
    equal = equal && (this.param == null ? that.param == null : this.param.equals(that.param));
    equal = equal && (this.val == null ? that.val == null : this.val.equals(that.val));
    return equal;
  }
  public boolean equals0(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof m_prod_params)) {
      return false;
    }
    m_prod_params that = (m_prod_params) o;
    boolean equal = true;
    equal = equal && (this.prod_id == null ? that.prod_id == null : this.prod_id.equals(that.prod_id));
    equal = equal && (this.param == null ? that.param == null : this.param.equals(that.param));
    equal = equal && (this.val == null ? that.val == null : this.val.equals(that.val));
    return equal;
  }
  public void readFields(ResultSet __dbResults) throws SQLException {
    this.__cur_result_set = __dbResults;
    this.prod_id = JdbcWritableBridge.readString(1, __dbResults);
    this.param = JdbcWritableBridge.readString(2, __dbResults);
    this.val = JdbcWritableBridge.readString(3, __dbResults);
  }
  public void readFields0(ResultSet __dbResults) throws SQLException {
    this.prod_id = JdbcWritableBridge.readString(1, __dbResults);
    this.param = JdbcWritableBridge.readString(2, __dbResults);
    this.val = JdbcWritableBridge.readString(3, __dbResults);
  }
  public void loadLargeObjects(LargeObjectLoader __loader)
      throws SQLException, IOException, InterruptedException {
  }
  public void loadLargeObjects0(LargeObjectLoader __loader)
      throws SQLException, IOException, InterruptedException {
  }
  public void write(PreparedStatement __dbStmt) throws SQLException {
    write(__dbStmt, 0);
  }

  public int write(PreparedStatement __dbStmt, int __off) throws SQLException {
    JdbcWritableBridge.writeString(prod_id, 1 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(param, 2 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(val, 3 + __off, 12, __dbStmt);
    return 3;
  }
  public void write0(PreparedStatement __dbStmt, int __off) throws SQLException {
    JdbcWritableBridge.writeString(prod_id, 1 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(param, 2 + __off, 12, __dbStmt);
    JdbcWritableBridge.writeString(val, 3 + __off, 12, __dbStmt);
  }
  public void readFields(DataInput __dataIn) throws IOException {
this.readFields0(__dataIn);  }
  public void readFields0(DataInput __dataIn) throws IOException {
    if (__dataIn.readBoolean()) { 
        this.prod_id = null;
    } else {
    this.prod_id = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.param = null;
    } else {
    this.param = Text.readString(__dataIn);
    }
    if (__dataIn.readBoolean()) { 
        this.val = null;
    } else {
    this.val = Text.readString(__dataIn);
    }
  }
  public void write(DataOutput __dataOut) throws IOException {
    if (null == this.prod_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, prod_id);
    }
    if (null == this.param) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, param);
    }
    if (null == this.val) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, val);
    }
  }
  public void write0(DataOutput __dataOut) throws IOException {
    if (null == this.prod_id) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, prod_id);
    }
    if (null == this.param) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, param);
    }
    if (null == this.val) { 
        __dataOut.writeBoolean(true);
    } else {
        __dataOut.writeBoolean(false);
    Text.writeString(__dataOut, val);
    }
  }
  private static final DelimiterSet __outputDelimiters = new DelimiterSet((char) 44, (char) 10, (char) 0, (char) 0, false);
  public String toString() {
    return toString(__outputDelimiters, true);
  }
  public String toString(DelimiterSet delimiters) {
    return toString(delimiters, true);
  }
  public String toString(boolean useRecordDelim) {
    return toString(__outputDelimiters, useRecordDelim);
  }
  public String toString(DelimiterSet delimiters, boolean useRecordDelim) {
    StringBuilder __sb = new StringBuilder();
    char fieldDelim = delimiters.getFieldsTerminatedBy();
    __sb.append(FieldFormatter.escapeAndEnclose(prod_id==null?"null":prod_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(param==null?"null":param, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(val==null?"null":val, delimiters));
    if (useRecordDelim) {
      __sb.append(delimiters.getLinesTerminatedBy());
    }
    return __sb.toString();
  }
  public void toString0(DelimiterSet delimiters, StringBuilder __sb, char fieldDelim) {
    __sb.append(FieldFormatter.escapeAndEnclose(prod_id==null?"null":prod_id, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(param==null?"null":param, delimiters));
    __sb.append(fieldDelim);
    __sb.append(FieldFormatter.escapeAndEnclose(val==null?"null":val, delimiters));
  }
  private static final DelimiterSet __inputDelimiters = new DelimiterSet((char) 9, (char) 10, (char) 0, (char) 0, false);
  private RecordParser __parser;
  public void parse(Text __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(CharSequence __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(byte [] __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(char [] __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(ByteBuffer __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  public void parse(CharBuffer __record) throws RecordParser.ParseError {
    if (null == this.__parser) {
      this.__parser = new RecordParser(__inputDelimiters);
    }
    List<String> __fields = this.__parser.parseRecord(__record);
    __loadFromFields(__fields);
  }

  private void __loadFromFields(List<String> fields) {
    Iterator<String> __it = fields.listIterator();
    String __cur_str = null;
    try {
    __cur_str = __it.next();
    if (__cur_str.equals("NULL")) { this.prod_id = null; } else {
      this.prod_id = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("NULL")) { this.param = null; } else {
      this.param = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("NULL")) { this.val = null; } else {
      this.val = __cur_str;
    }

    } catch (RuntimeException e) {    throw new RuntimeException("Can't parse input data: '" + __cur_str + "'", e);    }  }

  private void __loadFromFields0(Iterator<String> __it) {
    String __cur_str = null;
    try {
    __cur_str = __it.next();
    if (__cur_str.equals("NULL")) { this.prod_id = null; } else {
      this.prod_id = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("NULL")) { this.param = null; } else {
      this.param = __cur_str;
    }

    __cur_str = __it.next();
    if (__cur_str.equals("NULL")) { this.val = null; } else {
      this.val = __cur_str;
    }

    } catch (RuntimeException e) {    throw new RuntimeException("Can't parse input data: '" + __cur_str + "'", e);    }  }

  public Object clone() throws CloneNotSupportedException {
    m_prod_params o = (m_prod_params) super.clone();
    return o;
  }

  public void clone0(m_prod_params o) throws CloneNotSupportedException {
  }

  public Map<String, Object> getFieldMap() {
    Map<String, Object> __sqoop$field_map = new TreeMap<String, Object>();
    __sqoop$field_map.put("prod_id", this.prod_id);
    __sqoop$field_map.put("param", this.param);
    __sqoop$field_map.put("val", this.val);
    return __sqoop$field_map;
  }

  public void getFieldMap0(Map<String, Object> __sqoop$field_map) {
    __sqoop$field_map.put("prod_id", this.prod_id);
    __sqoop$field_map.put("param", this.param);
    __sqoop$field_map.put("val", this.val);
  }

  public void setField(String __fieldName, Object __fieldVal) {
    if ("prod_id".equals(__fieldName)) {
      this.prod_id = (String) __fieldVal;
    }
    else    if ("param".equals(__fieldName)) {
      this.param = (String) __fieldVal;
    }
    else    if ("val".equals(__fieldName)) {
      this.val = (String) __fieldVal;
    }
    else {
      throw new RuntimeException("No such field: " + __fieldName);
    }
  }
  public boolean setField0(String __fieldName, Object __fieldVal) {
    if ("prod_id".equals(__fieldName)) {
      this.prod_id = (String) __fieldVal;
      return true;
    }
    else    if ("param".equals(__fieldName)) {
      this.param = (String) __fieldVal;
      return true;
    }
    else    if ("val".equals(__fieldName)) {
      this.val = (String) __fieldVal;
      return true;
    }
    else {
      return false;    }
  }
}
