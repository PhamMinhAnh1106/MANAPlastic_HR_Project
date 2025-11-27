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
export function getAllSchedules() {
    const result: any[] = [];

    // Hàm tạo ca làm việc
    function generate(time: number, baseShiftId: number) {
        const duration = (time === 8) ? time + 1 : time;
        const prefix = `C${time}`;

        for (let i = 1; i <= 24; i++) {
            const startHour = i % 24;
            const endHour = (startHour + duration) % 24;

            const start = startHour.toString().padStart(2, "0") + ":00:00";
            const end = endHour.toString().padStart(2, "0") + ":00:00";

            result.push({
                shift_id: baseShiftId + i,
                shift_name: `${prefix}${i.toString().padStart(2, "0")}`,
                start_time: start,
                end_time: end,
                type: time // 6 hoặc 8
            });
        }
    }

    // Ca 6 tiếng
    generate(6, 4);

    // Ca 8 tiếng
    generate(8, 28);

    // ⭐ Thêm các loại nghỉ
    const leaveTypes = [
        { shift_id: 53, shift_name: "AL (Anually Leave)" },
        { shift_id: 54, shift_name: "SL (Sick Leave)" },
        { shift_id: 55, shift_name: "PL (Paternity Leave)" },
        { shift_id: 56, shift_name: "ML (Maternity Leave)" },
        { shift_id: 57, shift_name: "UL (Unpaid Leave)" }
    ];

    leaveTypes.forEach(l => {
        result.push({
            shift_id: l.shift_id,
            shift_name: l.shift_name,
            start_time: null,
            end_time: null,
            type: "leave"
        });
    });

    return result;
}

