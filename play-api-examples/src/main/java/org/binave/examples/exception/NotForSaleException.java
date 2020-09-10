package org.binave.examples.exception;

/**
 * @author by bin jin on 2017/3/27.
 */
public class NotForSaleException extends BusinessException {

    private long id;

    /**
     * @param id 道具id
     */
    public NotForSaleException(long id) {
        super("BaseItemConf id=" + id);
        this.id = id;
    }

    public NotForSaleException(long id, Throwable cause) {
        super("BaseItemConf id=" + id, cause);
        this.id = id;
    }

    @Override
    protected long getId() {
        return id;
    }

}
