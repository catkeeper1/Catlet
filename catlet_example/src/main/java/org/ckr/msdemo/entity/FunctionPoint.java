package org.ckr.msdemo.entity;




import javax.persistence.*;

/**
 * Created by Administrator on 2017/11/4.
 */
@Entity()
@Table(name = "FUNCTION_POINTA")
@Access(AccessType.FIELD)
public class FunctionPoint  {

    private static final long serialVersionUID = -8148548168711916671L;

    /**
     * The unique ID of function point.
     */
    @Id
    @Column(name = "FUN_POINT_CODE", unique = true, nullable = false, length = 100)
    private String functionPointCode;

    @Column(name = "FUN_POINT_DESCRIPTION", length = 100)
    private String functionPointDescription;

    private int intValue;

    private int[] intArray;

    public String getFunctionPointCode() {
        return functionPointCode;
    }

    public void setFunctionPointCode(String functionPointCode) {
        this.functionPointCode = functionPointCode;
    }


    public String getFunctionPointDescription() {
        return functionPointDescription;
    }

    public void setFunctionPointDescription(String functionPointDescription) {
        this.functionPointDescription = functionPointDescription;
    }




}
