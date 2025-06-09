package com.example.movement;

import com.example.movement.model.UserDTO;

public class SessionManagement {

        private static SessionManagement instance;
        private UserDTO currentUser;

        private SessionManagement() {}

        public static SessionManagement getInstance() {
            if (instance == null) {
                instance = new SessionManagement();
            }
            return instance;
        }

        public void setUser(UserDTO user) {
            this.currentUser = user;
        }

        public UserDTO getUser() {
            return currentUser;
        }

        public void clearUser() {
            currentUser = null;
        }
    }


