export {};

declare global {
  interface Window {
    harness: {
      save(): Promise<void>;
      start(): void;
      waitForStart(): Promise<void>;
      release(): void;
      result(): Promise<unknown>;
      recover(): Promise<unknown>;
      status(): Promise<string | undefined>;
    };
  }
}
