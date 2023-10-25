export const messages = {
    common : {
        required: "Required",
        emailInvalid: "Invalid email",
        phoneInvalid: "Invalid phone number",
        emailAlready: "Email is already used",
        phoneAlready: "Phone is already used",
        dobBeforeCurrent: "Your day of birth must be before current date",
        age18:"Not old enough to work (age >= 18)"
    },
    booking: {
        minSeat: "Must select at least 1 seat",
        cardInvalid: "Invalid Card Number e.g: '4111111111111'",
        expiredDate: "Invalid Expired Date e.g: MM/YY => 12/24",
        cvvInvalid: "Invalid CVV e.g: 123",
        success: "Add new Bookings successfully",
        updateSuccess: "Update Booking successfully"
    },
    trip: {
        soureSame: "Source is the same as Destination",
        destinationSame: "Destination is the same as Source",
        pricePos: "Price must be positive",
        success: "Add new trip successfully",
        updateSuccess: "Update trip successfully"
    },
    driver: {
        licenseReady: "License Number is already used",
        success: "Add new driver successfully",
        updateSuccess: "Update driver successfully"
    },
    coach: {
        nameReady: "Name is already used",
        capacityPos: "Capacity must be positive",
        licensePlateReady: "License plate is already used",
        success: "Add new coach successfully",
        updateSuccess: "Update coach successfully"
    },
    discount: {
        codeReady:"Code is already used",
        amountPos: "Amount must be positive",
        success: "Add new discount successfully",
        updateSuccess: "Update discount successfully"
    },
    users: {
        usernameReady: "Username is already used",
        success: "Add new user successfully",
        updateSuccess: "Update user successfully"
    }
}