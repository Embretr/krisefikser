package stud.ntnu.krisefikser.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.krisefikser.map.entity.Event;
import stud.ntnu.krisefikser.map.service.EventService;

import java.util.List;

/**
 * REST controller for managing events in the system.
 * Provides endpoints for CRUD operations on events.
 *
 * @since 1.0
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Event", description = "Event management APIs")
public class EventController {
    private final EventService eventService;

    /**
     * Retrieves all events from the system.
     *
     * @return ResponseEntity containing a list of all events.
     * @since 1.0
     */
    @Operation(summary = "Get all events", description = "Retrieves a list of all events in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved events", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class)))
    })
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    /**
     * Retrieves a specific event by its ID.
     *
     * @param id The ID of the event to retrieve.
     * @return ResponseEntity containing the requested event.
     * @since 1.0
     */
    @Operation(summary = "Get an event by ID", description = "Retrieves a specific event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the event", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(
            @Parameter(description = "ID of the event to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    /**
     * Creates a new event in the system.
     * Only accessible to users with ADMIN role.
     *
     * @param event The event to create.
     * @return ResponseEntity containing the created event.
     * @since 1.0
     */
    @Operation(summary = "Create a new event", description = "Creates a new event in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the event", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(
            @Parameter(description = "Event to create") @RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    /**
     * Updates an existing event in the system.
     * Only accessible to users with ADMIN role.
     *
     * @param id    The ID of the event to update.
     * @param event The updated event data.
     * @return ResponseEntity containing the updated event.
     * @since 1.0
     */
    @Operation(summary = "Update an event", description = "Updates an existing event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the event", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> updateEvent(
            @Parameter(description = "ID of the event to update") @PathVariable Long id,
            @Parameter(description = "Updated event details") @RequestBody Event event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    /**
     * Deletes an event from the system.
     * Only accessible to users with ADMIN role.
     *
     * @param id The ID of the event to delete.
     * @return ResponseEntity with no content.
     * @since 1.0
     */
    @Operation(summary = "Delete an event", description = "Deletes an event from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the event"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID of the event to delete") @PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}