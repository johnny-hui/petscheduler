package com.johnny.petscheduler.utils.users;

import com.johnny.petscheduler.DTO.registration.RegisterUserDTO;
import com.johnny.petscheduler.DTO.user.UserResponseDTO;
import com.johnny.petscheduler.security.user.UserRole;

/**
 * This utility class contains methods that help filter and create UserDTO responses.
 */
public final class UserResponseUtil
{
    private UserResponseUtil(){}

    /**
     * Creates and returns a UserResponseDTO according to a user's role.
     *
     * @param request
     *      A RegisterUserDTO object
     *
     * @param role
     *      A UserRole enum value
     *
     * @return <code>UserResponseDTO</code>
     *      A UserResponseDTO object
     */
    public static UserResponseDTO createUserResponseDTO(final RegisterUserDTO request,
                                                        final UserRole role)
    {
        return new UserResponseDTO(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                role,
                request.getCountry(),
                request.getCity(),
                request.getTimezone(),
                request.getAvatarURL()
        );
    }
}
