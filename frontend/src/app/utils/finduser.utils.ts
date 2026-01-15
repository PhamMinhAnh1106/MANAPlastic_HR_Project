import { api } from "../services/api.service";


export async function findUserbyUsername(username: string) {
    try {
        if (!username || username.trim() === '') return null;
        const res = await api.get(`/manager/searchUsers?keyword=${username}`);
        return res.data;
    } catch (error) {
        return error;
    }

}

// <input type="text"
//                      [(ngModel)]="newEmployeeForm.username"
//                      (input)="onSearchUser($event)"
//                      placeholder="Ví dụ: nguyenvana"
//                      class="custom-input-lg"
//                      autocomplete="off">
// onSearchUser(event: any) {
//     const value = event.target.value;
//     // Nếu rỗng thì ẩn dropdown
//     if (!value || value.trim() === '') {
//       this.showUserSearchDropdown = false;
//       this.userSearchResults = [];
//       return;
//     }
//     // Push vào subject để debounce
//     this.searchSubject.next(value);
//   }

//   async performUserSearch(username: string) {
//     try {
//       // Gọi hàm API được yêu cầu
//       const res = await findUserbyUsername(username) as any[];

//       // Giới hạn 5 kết quả
//       if (res && Array.isArray(res)) {
//         this.userSearchResults = res.slice(0, 5);
//         this.showUserSearchDropdown = this.userSearchResults.length > 0;
//       } else {
//         this.userSearchResults = [];
//         this.showUserSearchDropdown = false;
//       }
//     } catch (error) {
//       console.error(error);
//       this.userSearchResults = [];
//       this.showUserSearchDropdown = false;
//     }
//     this.cdr.detectChanges();
//   }

//   selectSearchUser(user: any) {
//     // Gán giá trị vào form
//     this.newEmployeeForm.username = user.username || user.fullName || '';
//     this.showUserSearchDropdown = false;
//   }