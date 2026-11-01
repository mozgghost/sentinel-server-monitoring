package com.sentinel.api.dto.monitoringgroup;

import java.util.UUID;

public record OwnerSessionResponse(
        UUID guestSessionId
) {
}