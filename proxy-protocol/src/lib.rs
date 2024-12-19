pub use ::binrw;

use binrw::binrw;

#[binrw]
#[brw(big)]
pub enum VibrateKind {
    #[brw(magic(0u32))]
    BlockBroken
}

#[binrw]
#[brw(big)]
#[derive(Debug, Clone)]
pub enum ProxyMessage {
    #[brw(magic(1u32))]
    Add { index: u32, position: (f32, f32) },
    #[brw(magic(2u32))]
    Remove { index: u32 },
    #[brw(magic(3u32))]
    Clear,
    #[brw(magic(4u32))]
    Vibrate
}
