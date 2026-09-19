import { useEffect, useRef, useState } from "react";

import {
    IconButton,
    Badge,
    Popover,
    Box,
    Typography,
    List,
    ListItemButton,
    ListItemText,
    Divider,
    Button,
    CircularProgress
} from "@mui/material";

import NotificationsIcon from "@mui/icons-material/Notifications";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";

import notificationService from "../../services/notificationService";

const POLL_INTERVAL_MS = 30000;

const formatDateTime = (value) => {

    if (!value) return "";

    try {

        return new Date(value).toLocaleString(undefined, {
            dateStyle: "medium",
            timeStyle: "short"
        });

    } catch {

        return value;
    }
};

const typeIcon = (type) => {

    if (type === "TIMETABLE_APPROVED") {

        return <CheckCircleIcon fontSize="small" color="success" />;
    }

    if (type === "TIMETABLE_REJECTED") {

        return <CancelIcon fontSize="small" color="error" />;
    }

    return <NotificationsIcon fontSize="small" color="disabled" />;
};

/**
 * Bell icon + unread badge, shown in the Topbar for every logged-in
 * role. The backend scopes "my notifications" to whoever is
 * authenticated, so this component doesn't need to know or care
 * about the current user's role - it just shows whatever came back.
 */
const NotificationBell = () => {

    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [anchorEl, setAnchorEl] = useState(null);

    const pollRef = useRef(null);

    const loadUnreadCount = async () => {

        try {

            const count = await notificationService.getUnreadCount();
            setUnreadCount(count);

        } catch (err) {

            console.error("Unable to load unread notification count:", err);

        }
    };

    const loadNotifications = async () => {

        try {

            setLoading(true);

            const data = await notificationService.getMyNotifications();
            setNotifications(Array.isArray(data) ? data : []);

        } catch (err) {

            console.error("Unable to load notifications:", err);

        } finally {

            setLoading(false);

        }
    };

    useEffect(() => {

        loadUnreadCount();

        pollRef.current = setInterval(loadUnreadCount, POLL_INTERVAL_MS);

        return () => clearInterval(pollRef.current);

    }, []);

    const handleOpen = (event) => {

        setAnchorEl(event.currentTarget);
        loadNotifications();

    };

    const handleClose = () => {

        setAnchorEl(null);

    };

    const handleNotificationClick = async (notification) => {

        if (!notification.read) {

            try {

                await notificationService.markNotificationAsRead(
                    notification.id
                );

                setNotifications((prev) =>
                    prev.map((n) =>
                        n.id === notification.id
                            ? { ...n, read: true }
                            : n
                    )
                );

                setUnreadCount((prev) => Math.max(0, prev - 1));

            } catch (err) {

                console.error("Unable to mark notification as read:", err);

            }
        }
    };

    const handleMarkAllRead = async () => {

        try {

            await notificationService.markAllNotificationsAsRead();

            setNotifications((prev) =>
                prev.map((n) => ({ ...n, read: true }))
            );

            setUnreadCount(0);

        } catch (err) {

            console.error("Unable to mark all notifications as read:", err);

        }
    };

    const open = Boolean(anchorEl);

    return (

        <>

            <IconButton
                color="inherit"
                onClick={handleOpen}
                sx={{ mr: 1 }}
            >

                <Badge badgeContent={unreadCount} color="error">
                    <NotificationsIcon />
                </Badge>

            </IconButton>

            <Popover
                open={open}
                anchorEl={anchorEl}
                onClose={handleClose}
                anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
                transformOrigin={{ vertical: "top", horizontal: "right" }}
            >

                <Box sx={{ width: 360, maxHeight: 420, overflowY: "auto" }}>

                    <Box
                        sx={{
                            px: 2,
                            py: 1.5,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "space-between"
                        }}
                    >

                        <Typography variant="subtitle1" fontWeight="bold">
                            Notifications
                        </Typography>

                        {unreadCount > 0 && (

                            <Button size="small" onClick={handleMarkAllRead}>
                                Mark all read
                            </Button>

                        )}

                    </Box>

                    <Divider />

                    {loading && (

                        <Box display="flex" justifyContent="center" py={3}>
                            <CircularProgress size={24} />
                        </Box>

                    )}

                    {!loading && notifications.length === 0 && (

                        <Box sx={{ p: 3, textAlign: "center" }}>

                            <Typography variant="body2" color="text.secondary">
                                You're all caught up.
                            </Typography>

                        </Box>

                    )}

                    {!loading && notifications.length > 0 && (

                        <List disablePadding>

                            {notifications.map((notification) => (

                                <ListItemButton
                                    key={notification.id}
                                    onClick={() =>
                                        handleNotificationClick(notification)
                                    }
                                    sx={{
                                        alignItems: "flex-start",
                                        backgroundColor: notification.read
                                            ? "transparent"
                                            : "action.hover"
                                    }}
                                >

                                    <Box sx={{ mr: 1.5, mt: 0.5 }}>
                                        {typeIcon(notification.type)}
                                    </Box>

                                    <ListItemText
                                        primary={notification.title}
                                        secondary={
                                            <>
                                                <Typography
                                                    component="span"
                                                    variant="body2"
                                                    color="text.secondary"
                                                    sx={{ display: "block" }}
                                                >
                                                    {notification.message}
                                                </Typography>

                                                <Typography
                                                    component="span"
                                                    variant="caption"
                                                    color="text.disabled"
                                                >
                                                    {formatDateTime(
                                                        notification.createdAt
                                                    )}
                                                </Typography>
                                            </>
                                        }
                                    />

                                </ListItemButton>

                            ))}

                        </List>

                    )}

                </Box>

            </Popover>

        </>

    );

};

export default NotificationBell;
