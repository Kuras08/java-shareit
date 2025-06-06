package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
public class BookingMapper {

    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingMapper(ItemMapper itemMapper, UserMapper userMapper) {
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(userMapper.toUserDto(booking.getBooker()))
                .item(itemMapper.toItemDto(booking.getItem())) // вызов через объект
                .build();
    }
}
