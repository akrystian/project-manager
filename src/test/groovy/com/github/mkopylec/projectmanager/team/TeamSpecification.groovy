package com.github.mkopylec.projectmanager.team

import com.github.mkopylec.projectmanager.common.Specification
import com.github.mkopylec.projectmanager.common.core.EventPublisher
import com.github.mkopylec.projectmanager.team.inbound.local.ProjectEventHandler
import com.github.mkopylec.projectmanager.team.utils.api.TeamHttpClient
import org.springframework.beans.factory.annotation.Autowired

abstract class TeamSpecification extends Specification {

    @Autowired
    protected TeamHttpClient team

    @Autowired
    protected EventPublisher eventPublisher

    @Autowired
    protected ProjectEventHandler projectEventHandler
}
