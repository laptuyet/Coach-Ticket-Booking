import * as yup from 'yup'
import { APP_CONSTANTS } from "../../../utils/appContants"
import { isAfter, parse } from 'date-fns'
import { messages as msg } from '../../../utils/validationMessages'

export default [
    yup.object().shape({
        trip: yup.object().required(msg.common.required),
        source: yup.object().required(msg.required),
        destination: yup.object().required(msg.required),
        from: yup.date().required(msg.required),
        to: yup.date().required(msg.required),
        bookingDateTime: yup.date().notRequired(),
        bookingType: yup.string().notRequired()
    }),
    yup.object().shape({
        seatNumber: yup.array().required(msg.required).min(1, msg.booking.minSeat),
    }),
    yup.object().shape({
        pickUpAddress: yup.string().required(msg.required),
        firstName: yup.string().required(msg.required),
        lastName: yup.string().required(msg.required),
        phone: yup
            .string()
            .matches(APP_CONSTANTS.PHONE_REGEX, msg.common.phoneInvalid)
            .required(msg.required),
        email: yup
            .string()
            .required(msg.required)
            .email(msg.common.emailInvalid),
        totalPayment: yup.number().notRequired(),
        paymentDateTime: yup.date().notRequired(),
        paymentMethod: yup.string().required(msg.required),
        paymentStatus: yup.string().notRequired(),
        nameOnCard: yup.string().when('paymentMethod', {
            is: 'CARD',
            then: () =>
                yup.string().required(msg.required)
            ,
            otherwise: () => yup.string().notRequired()
        }),
        cardNumber: yup.string().when('paymentMethod', {
            is: "CARD",
            then: () =>
                yup.string().required(msg.common.required).matches(APP_CONSTANTS.VISA_REGEX, msg.booking.cardInvalid)
            ,
            otherwise: () => yup.string().notRequired()
        }),
        expiredDate: yup.string().when('paymentMethod', {
            is: "CARD",
            then: () =>
                yup.string().required(msg.common.required)
                    .test('expiredDate', msg.booking.expiredDate, (value) => {
                        const expirationDate = parse(value, 'MM/yy', new Date());
                        return isAfter(expirationDate, new Date());
                    })
            ,
            otherwise: () => yup.string().notRequired()
        }),
        cvv: yup.string().when('paymentMethod', {
            is: "CARD",
            then: () =>
                yup.string().required(msg.common.required)
                    .test('len', msg.booking.cvvInvalid, (value) => value && value.length === 3)
            ,
            otherwise: () => yup.string().notRequired()
        })
    })
]