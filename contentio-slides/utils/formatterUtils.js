import TimeAgo from 'javascript-time-ago'
import en from 'javascript-time-ago/locale/en'

TimeAgo.addLocale(en)
const timeAgoFormatter = new TimeAgo('en-US')

export const timeAgo = date => timeAgoFormatter.format(date)

export const kNumber = num =>{
    return Math.abs(num) > 999 ? (Math.sign(num) * ((Math.abs(num) / 1000))).toFixed(1) + 'k' : Math.sign(num) * Math.abs(num)

}