package com.oceanview.dao;

import com.oceanview.model.GuestReservation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Types;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class GuestReservationGatewayTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStmt;
    @Mock private CallableStatement mockCallStmt;
    @Mock private ResultSet mockResultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockPreparedStmt);
        when(mockConnection.prepareCall(anyString()))
                .thenReturn(mockCallStmt);
    }

    // Test: GenerateReservationNumber stored procedure
    @Test
    public void testGenerateReservationNumber()
            throws Exception {

        when(mockCallStmt.getString(1))
                .thenReturn("RES-2026-00001");

        mockCallStmt.registerOutParameter(
                1, Types.VARCHAR);
        mockCallStmt.execute();
        String result = mockCallStmt.getString(1);

        assertNotNull("Generated number should "
                + "not be null", result);
        assertTrue("Number should start with RES-",
                result.startsWith("RES-"));
        assertEquals("RES-2026-00001", result);
    }

    // Test: Find reservation returns data
    @Test
    public void testFindReservationByIdReturnsData()
            throws Exception {

        when(mockPreparedStmt.executeQuery())
                .thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("reservation_id"))
                .thenReturn(1);
        when(mockResultSet.getString("reservation_number"))
                .thenReturn("RES-2026-00001");
        when(mockResultSet.getString("guest_name"))
                .thenReturn("Kasun Perera");
        when(mockResultSet.getString("room_type"))
                .thenReturn("Standard");
        when(mockResultSet.getString("reservation_status"))
                .thenReturn("Confirmed");
        when(mockResultSet.getInt("number_of_nights"))
                .thenReturn(3);

        mockPreparedStmt.setInt(1, 1);
        ResultSet result =
                mockPreparedStmt.executeQuery();

        assertTrue(result.next());
        assertEquals("RES-2026-00001",
                result.getString("reservation_number"));
        assertEquals("Kasun Perera",
                result.getString("guest_name"));
        assertEquals("Standard",
                result.getString("room_type"));
        assertEquals(3,
                result.getInt("number_of_nights"));
    }

    // Test: Find reservation returns null for no data
    @Test
    public void testFindReservationByIdReturnsNull()
            throws Exception {

        when(mockPreparedStmt.executeQuery())
                .thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        mockPreparedStmt.setInt(1, 999);
        ResultSet result =
                mockPreparedStmt.executeQuery();

        assertFalse("No data should return false",
                result.next());
    }

    // Test: Insert reservation returns generated key
    @Test
    public void testInsertReservationReturnsId()
            throws Exception {

        when(mockConnection.prepareStatement(
                anyString(), anyInt()))
                .thenReturn(mockPreparedStmt);
        when(mockPreparedStmt.executeUpdate())
                .thenReturn(1);
        when(mockPreparedStmt.getGeneratedKeys())
                .thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);

        int rowsInserted =
                mockPreparedStmt.executeUpdate();
        assertEquals("One row should be inserted",
                1, rowsInserted);

        ResultSet generatedKeys =
                mockPreparedStmt.getGeneratedKeys();
        assertTrue(generatedKeys.next());
        int newId = generatedKeys.getInt(1);
        assertEquals("Generated ID should be 5",
                5, newId);
    }

    // Test: Update reservation status
    @Test
    public void testUpdateReservationStatus()
            throws Exception {

        when(mockPreparedStmt.executeUpdate())
                .thenReturn(1);

        mockPreparedStmt.setString(1, "Checked-In");
        mockPreparedStmt.setString(2, null);
        mockPreparedStmt.setInt(3, 1);

        int rowsUpdated =
                mockPreparedStmt.executeUpdate();

        assertEquals("One row should be updated",
                1, rowsUpdated);
    }

    // Test: Count reservations by status
    @Test
    public void testGetReservationCountByStatus()
            throws Exception {

        when(mockPreparedStmt.executeQuery())
                .thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("status_count"))
                .thenReturn(5);

        mockPreparedStmt.setString(1, "Confirmed");
        ResultSet result =
                mockPreparedStmt.executeQuery();

        assertTrue(result.next());
        assertEquals("Should have 5 confirmed",
                5, result.getInt("status_count"));
    }

    // Test: CalculateBill stored procedure
    @Test
    public void testCalculateBillStoredProcedure()
            throws Exception {

        when(mockCallStmt.getString(2))
                .thenReturn("BILL-2026-00001");
        when(mockCallStmt.getDouble(3))
                .thenReturn(16500.00);
        when(mockCallStmt.getDouble(4))
                .thenReturn(0.00);
        when(mockCallStmt.getDouble(5))
                .thenReturn(16500.00);

        mockCallStmt.setInt(1, 1);
        mockCallStmt.registerOutParameter(
                2, Types.VARCHAR);
        mockCallStmt.registerOutParameter(
                3, Types.DECIMAL);
        mockCallStmt.registerOutParameter(
                4, Types.DECIMAL);
        mockCallStmt.registerOutParameter(
                5, Types.DECIMAL);
        mockCallStmt.execute();

        assertEquals("BILL-2026-00001",
                mockCallStmt.getString(2));
        assertEquals(16500.00,
                mockCallStmt.getDouble(3), 0.01);
        assertEquals(16500.00,
                mockCallStmt.getDouble(5), 0.01);
    }
}