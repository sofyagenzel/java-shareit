package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    @FutureOrPresent
    private LocalDateTime start;
    @Column(name = "end_date")
    @FutureOrPresent
    private LocalDateTime end;
    @OneToOne
    private Item item;
    @OneToOne
    private User booker;
    @Enumerated(EnumType.STRING)
    private StatusBooking status;

    public Booking(Booking newBooking) {
        this.setId(newBooking.getId());
        this.setBooker(newBooking.getBooker());
        this.setStart(newBooking.getStart());
        this.setEnd(newBooking.getEnd());
        this.setStatus(newBooking.getStatus());
        this.setItem(newBooking.getItem());
    }
}