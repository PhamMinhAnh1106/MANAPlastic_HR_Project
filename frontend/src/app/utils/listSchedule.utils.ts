export function scheduleList(time: number, list: any) {

    // Nếu time = 8 thì cộng 9 giờ
    const duration = (time === 8) ? time + 1 : time;

    // Tạo prefix C601 hoặc C801
    const prefix = `C${time}`;
    let shiftId: number = 0;
    if (time == 6) {
        list.length = 0;
        shiftId = 4;
    } else {
        list.length = 0;
        shiftId = 28;
    }
    for (let i = 1; i <= 24; i++) {

        // Giờ bắt đầu
        const startHour = i % 24;

        // Giờ kết thúc (quay vòng 24h)
        const endHour = (startHour + duration) % 24;

        // Format 01:00:00
        const start = startHour.toString().padStart(2, "0") + ":00:00";
        const end = endHour.toString().padStart(2, "0") + ":00:00";

        list.push({
            shift_id: shiftId + i,
            shift_name: `${prefix}${i.toString().padStart(2, "0")}`,
            start_time: start,
            end_time: end
        });
    }

    return list;
}