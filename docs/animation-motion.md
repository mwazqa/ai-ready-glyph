# Animation motion

Notify Glyph 0.9.0 retains the six-second playback duration. Every style keeps its previous opening and hold through 4800 ms, then uses the final 1200 ms to close its shape. The final central dot in Claude and Gemini also has a short brightness fade after the symbol contracts.

| Style | Closing motion |
| --- | --- |
| ChatGPT | Strokes unwind from the outer parts back to the inner junctions. |
| Claude | The current bloom cycle finishes its contraction, then the small central shape shrinks away without starting another bloom. |
| Gemini | The star contracts to a single pixel, which disappears. |
| Other AI | Lettering erases from right to left. |
| Discord | The two halves slide apart and leave the circular Matrix. |
| Messenger | The bubble contracts, leaving the bolt, which erases in reverse. |
| WhatsApp | The bubble and handset reverse their drawing order. |
| SMS | Dots withdraw from right to left, then the bubble outline closes. |
| Instagram | The flash withdraws, the lens contracts and camera corners un-draw. |
| TikTok | The note un-draws from the top towards its base. |
| X | The centre opens and diagonal strokes withdraw towards their tips. |
| Facebook | The crossbar retracts, then the remaining stem un-draws. |

All pixels are cleared at 6000 ms. The automatic six-second start delay is separate from playback and is unchanged. Manual and Glyph Button tests remain immediate. A newer accepted alert can still replace a pending or playing animation; there is no backlog queue.

The renderer remains circular, 25 x 25 and monochrome. Unit tests check brightness bounds, shape-changing exits and complete clearing. Generated GIFs use the actual Java renderer; they do not confirm physical LED output or Nothing OS effect priority.
