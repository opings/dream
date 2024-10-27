package zyz.free.data.entity;

import ch.qos.logback.core.joran.event.SaxEventRecorder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class IdSegmentEntity implements Serializable {
    private Long id;

    private String bizTag;

    private Long maxId;

    private Long pStep;

    private Date lastUpdateTime;

    private Date currentUpdateTime;

    private Long oldMaxId;

}