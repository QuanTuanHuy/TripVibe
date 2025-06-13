// Simple user store to map userId -> userName
export class UserStore {
  private users = new Map<number, string>();

  setUser(userId: number, userName: string) {
    this.users.set(userId, userName);
  }

  getUser(userId: number): string {
    return this.users.get(userId) || `User ${userId}`;
  }

  removeUser(userId: number) {
    this.users.delete(userId);
  }

  clear() {
    this.users.clear();
  }
}

export const userStore = new UserStore();
